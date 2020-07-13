package com.oopsjpeg.snubot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oopsjpeg.snubot.command.CommandManager;
import com.oopsjpeg.snubot.command.CommandRegistry;
import com.oopsjpeg.snubot.command.impl.dev.SaveAllCommand;
import com.oopsjpeg.snubot.command.impl.general.ColorCommand;
import com.oopsjpeg.snubot.command.impl.general.HelpCommand;
import com.oopsjpeg.snubot.command.impl.general.LevelCommand;
import com.oopsjpeg.snubot.command.impl.mod.LogCommand;
import com.oopsjpeg.snubot.command.impl.mod.ModRoleCommand;
import com.oopsjpeg.snubot.command.impl.mod.ReactIonRolesCommand;
import com.oopsjpeg.snubot.data.SaveData;
import com.oopsjpeg.snubot.data.impl.GuildData;
import com.oopsjpeg.snubot.data.impl.UserData;
import com.oopsjpeg.snubot.manager.Manager;
import com.oopsjpeg.snubot.manager.impl.LevelManager;
import com.oopsjpeg.snubot.manager.impl.LogManager;
import com.oopsjpeg.snubot.manager.impl.MongoManager;
import com.oopsjpeg.snubot.react.ReactManager;
import com.oopsjpeg.snubot.util.BadSettingsException;
import com.oopsjpeg.snubot.util.Settings;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.oopsjpeg.snubot.util.Settings.*;

public class Snubot
{
    public static final Logger LOGGER = LoggerFactory.getLogger(Snubot.class);
    public static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() + 1);
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static Snubot instance;

    private final List<Manager> managerList = new ArrayList<>();
    private final Map<String, UserData> userDataMap = new HashMap<>();
    private final Map<String, GuildData> guildDataMap = new HashMap<>();

    private Settings settings;
    private GatewayDiscordClient gateway;

    public static void main(String[] args) throws IOException, BadSettingsException
    {
        instance = new Snubot();
        instance.start();
    }

    public static File getSettingsFile()
    {
        return new File("snubot.properties");
    }

    //public static Snubot getInstance()
    //{
    //    return instance;
    //}

    private void start() throws IOException, BadSettingsException
    {
        loadSettings();

        // Create client and log in
        DiscordClient client = DiscordClient.create(settings.get(TOKEN));
        gateway = client.login().block();
        // Set up ready event actions
        gateway.on(ReadyEvent.class).subscribe(e ->
        {
            // Create command registry and add commands
            CommandRegistry registry = new CommandRegistry(settings.get(PREFIX));
            registry.getCommandSet().addAll(Arrays.asList(new HelpCommand(), new LevelCommand(), new ReactIonRolesCommand(),
                    new SaveAllCommand(), new ModRoleCommand(), new LogCommand(), new ColorCommand()));
            // Create managers
            registerManager(new MongoManager(this, settings.get(MONGO_HOST), settings.get(MONGO_DATABASE)));
            registerManager(new CommandManager(this, registry));
            registerManager(new LevelManager(this));
            registerManager(new ReactManager(this));
            registerManager(new LogManager(this));
            // Load data
            userDataMap.putAll(getMongoManager().fetchUserDataMap());
            guildDataMap.putAll(getMongoManager().fetchGuildDataMap());
            getReactManager().getMessageMap().putAll(getMongoManager().fetchReactMessageMap());

            // Save data every 5 minutes
            SCHEDULER.scheduleAtFixedRate(this::saveAll, 1, 1, TimeUnit.MINUTES);
            // Save data on shut down
            Runtime.getRuntime().addShutdownHook(new Thread(this::saveAll));

            LOGGER.info("Logged in as " + e.getSelf().getUsername() + ".");
        });
        // Handle disconnects
        gateway.onDisconnect().block();
    }

    private void loadSettings() throws IOException, BadSettingsException
    {
        Snubot.LOGGER.info("Loading settings.");
        settings = new Settings();
        // Store new settings if it doesn't exist
        if (!getSettingsFile().exists())
        {
            try (FileWriter fw = new FileWriter(getSettingsFile()))
            {
                settings.store(fw);
                throw new BadSettingsException("Created new settings file (" + getSettingsFile().getName() + ")");
            }
        }
        // Load settings
        try (FileReader fr = new FileReader(getSettingsFile()))
        {
            settings.load(fr);
            // Validate each setting
            if (settings.get(TOKEN).isEmpty()) throw new BadSettingsException("Token cannot be empty");
            if (settings.get(PREFIX).isEmpty()) throw new BadSettingsException("Prefix cannot be empty");
            if (settings.get(MONGO_DATABASE).isEmpty())
                throw new BadSettingsException("MongoDB database name cannot be empty");
        }
    }

    public void saveAll()
    {
        userDataMap.values().stream().filter(SaveData::isMarkedForSave).forEach(getMongoManager()::saveUserData);
        guildDataMap.values().stream().filter(SaveData::isMarkedForSave).forEach(getMongoManager()::saveGuildData);
        getReactManager().getMessageMap().values().stream().filter(SaveData::isMarkedForSave).forEach(getMongoManager()::saveReactMessage);
    }

    public List<Manager> getManagerList()
    {
        return managerList;
    }

    public <T extends Manager> T getManager(Class<? extends T> clazz)
    {
        return (T) managerList.stream().filter(m -> m.getClass().equals(clazz)).findAny().orElse(null);
    }

    public void registerManager(Manager manager)
    {
        managerList.add(manager);
        manager.register(gateway);
    }

    public MongoManager getMongoManager()
    {
        return getManager(MongoManager.class);
    }

    public CommandManager getCommandManager()
    {
        return getManager(CommandManager.class);
    }

    public LevelManager getLevelManager()
    {
        return getManager(LevelManager.class);
    }

    public ReactManager getReactManager()
    {
        return getManager(ReactManager.class);
    }

    public Map<String, UserData> getUserDataMap()
    {
        return userDataMap;
    }

    public Map<String, GuildData> getGuildDataMap()
    {
        return guildDataMap;
    }

    public UserData getUserData(String id)
    {
        return (UserData) userDataMap.get(id).parent(this);
    }

    public UserData getUserData(Snowflake id)
    {
        return getUserData(id.asString());
    }

    public UserData getUserData(User user)
    {
        return getUserData(user.getId().asString());
    }

    public GuildData getGuildData(String id)
    {
        return (GuildData) guildDataMap.get(id).parent(this);
    }

    public GuildData getGuildData(Snowflake id)
    {
        return getGuildData(id.asString());
    }

    public GuildData getGuildData(Guild guild)
    {
        return getGuildData(guild.getId());
    }

    public UserData addUserData(String id)
    {
        userDataMap.put(id, new UserData(id));
        return getUserData(id);
    }

    public UserData addUserData(Snowflake id)
    {
        return addUserData(id.asString());
    }

    public UserData addUserData(User user)
    {
        return addUserData(user.getId());
    }

    public GuildData addGuildData(String id)
    {
        guildDataMap.put(id, new GuildData(id));
        return getGuildData(id);
    }

    public GuildData addGuildData(Snowflake id)
    {
        return addGuildData(id.asString());
    }

    public GuildData addGuildData(Guild guild)
    {
        return addGuildData(guild.getId());
    }

    public void removeUserData(String id)
    {
        userDataMap.remove(id);
    }

    public void removeUserData(Snowflake id)
    {
        removeUserData(id.asString());
    }

    public void removeUserData(User user)
    {
        removeUserData(user.getId());
    }

    public void removeGuildData(String id)
    {
        guildDataMap.remove(id);
    }

    public void removeGuildData(Snowflake id)
    {
        removeGuildData(id.asString());
    }

    public void removeGuildData(Guild guild)
    {
        removeGuildData(guild.getId());
    }

    public boolean hasUserData(String id)
    {
        return userDataMap.containsKey(id);
    }

    public boolean hasUserData(Snowflake id)
    {
        return hasUserData(id.asString());
    }

    public boolean hasUserData(User user)
    {
        return hasUserData(user.getId());
    }

    public boolean hasGuildData(String id)
    {
        return guildDataMap.containsKey(id);
    }

    public boolean hasGuildData(Snowflake id)
    {
        return hasGuildData(id.asString());
    }

    public boolean hasGuildData(Guild guild)
    {
        return hasGuildData(guild.getId());
    }

    public UserData getOrAddUserData(String id)
    {
        if (!hasUserData(id))
            return addUserData(id);
        return getUserData(id);
    }

    public UserData getOrAddUserData(Snowflake id)
    {
        return getOrAddUserData(id.asString());
    }

    public UserData getOrAddUserData(User user)
    {
        return getOrAddUserData(user.getId());
    }

    public GuildData getOrAddGuildData(String id)
    {
        if (!hasGuildData(id))
            return addGuildData(id);
        return getGuildData(id);
    }

    public GuildData getOrAddGuildData(Snowflake id)
    {
        return getOrAddGuildData(id.asString());
    }

    public GuildData getOrAddGuildData(Guild guild)
    {
        return getOrAddGuildData(guild.getId());
    }

    public Settings getSettings()
    {
        return settings;
    }

    public GatewayDiscordClient getGateway()
    {
        return gateway;
    }
}
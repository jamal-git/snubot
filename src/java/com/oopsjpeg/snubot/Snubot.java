package com.oopsjpeg.snubot;

import com.google.auth.oauth2.GoogleCredentials;
import com.oopsjpeg.snubot.command.CommandEnum;
import com.oopsjpeg.snubot.command.CommandListener;
import com.oopsjpeg.snubot.data.UserData;
import com.oopsjpeg.snubot.react.ReactController;
import com.oopsjpeg.snubot.util.BadSettingsException;
import com.oopsjpeg.snubot.util.Settings;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.core.object.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Snubot
{
    public static final Logger LOGGER = LoggerFactory.getLogger(Snubot.class);
    public static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() + 1);

    private static Snubot instance;

    private Settings settings;
    private FirestoreManager firestore;
    private GatewayDiscordClient gateway;
    private CommandListener commandListener;
    private ReactController reactController;

    private List<UserData> userDataList;

    public static void main(String[] args) throws IOException, BadSettingsException
    {
        instance = new Snubot();
        instance.start();
    }

    public static File getSettingsFile()
    {
        return new File("snubot.properties");
    }

    public static Snubot getInstance()
    {
        return instance;
    }

    private void start() throws IOException, BadSettingsException
    {
        loadSettings();
        loadFirestore();

        // Create client and log in
        DiscordClient client = DiscordClient.create(settings.get(Settings.TOKEN));
        gateway = client.login().block();
        // Set up ready event actions
        gateway.on(ReadyEvent.class).subscribe(e -> {
            userDataList = firestore.fetchUserDataList();
            reactController.setContainerList(firestore.fetchReactContainerList());
            LOGGER.info("Logged in as " + e.getSelf().getUsername() + ".");
        });
        // Create command listener and add commands
        commandListener = new CommandListener(settings.get(Settings.PREFIX));
        commandListener.getCommandSet().addAll(Arrays.asList(CommandEnum.values()));
        gateway.on(MessageCreateEvent.class).subscribe(commandListener::onMessage);
        // Create reaction controller
        reactController = new ReactController();
        gateway.on(ReactionAddEvent.class).subscribe(reactController::onReactAdd);
        gateway.on(ReactionRemoveEvent.class).subscribe(reactController::onReactRemove);
        // Handle disconnect
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
            if (settings.get(Settings.TOKEN).isEmpty()) throw new BadSettingsException("Token cannot be empty");
            if (settings.get(Settings.PREFIX).isEmpty()) throw new BadSettingsException("Prefix cannot be empty");
        }
    }

    private void loadFirestore() throws IOException
    {
        Snubot.LOGGER.info("Creating Firestore manager.");
        try (FileInputStream serviceAccount = new FileInputStream("firebase.json"))
        {
            GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
            firestore = new FirestoreManager(credentials);
        }
    }

    public Settings getSettings()
    {
        return settings;
    }

    public FirestoreManager getFirestore()
    {
        return firestore;
    }

    public GatewayDiscordClient getGateway()
    {
        return gateway;
    }

    public CommandListener getCommandListener()
    {
        return commandListener;
    }

    public ReactController getReactController()
    {
        return reactController;
    }

    public List<UserData> getUserDataList()
    {
        return userDataList;
    }

    public UserData getUserData(User user)
    {
        if (!hasUserData(user)) addUserData(user);
        return userDataList.stream().filter(data -> data.getId().equals(user.getId().asString())).findAny().orElse(null);
    }

    public void addUserData(User user)
    {
        userDataList.add(new UserData(user.getId().asString()));
    }

    public boolean hasUserData(User user)
    {
        return userDataList.stream().anyMatch(data -> data.getId().equals(user.getId().asString()));
    }
}
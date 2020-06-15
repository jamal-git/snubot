package com.oopsjpeg.snubot;

import com.oopsjpeg.snubot.command.CommandDispatcher;
import com.oopsjpeg.snubot.command.CommandEnum;
import com.oopsjpeg.snubot.util.BadSettingsException;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class Snubot {
    private static Snubot instance;

    private Settings settings;
    private GatewayDiscordClient gateway;
    private CommandDispatcher dispatcher;

    public static void main(String[] args) throws IOException, BadSettingsException {
        instance = new Snubot();
        instance.start();
    }

    public static File getSettingsFile() {
        return new File("snubot.properties");
    }

    public static Snubot getInstance() {
        return instance;
    }

    private void start() throws IOException, BadSettingsException {
        settings = new Settings();
        loadSettings();

        // Create client
        DiscordClient client = DiscordClient.create(settings.get(Settings.TOKEN));
        // Create dispatcher
        dispatcher = new CommandDispatcher(settings.get(Settings.PREFIX));
        // Register commands
        dispatcher.getRegistry().getCommands().addAll(Arrays.asList(CommandEnum.values()));
        // Log in and register dispatcher
        gateway = client.login().block();
        gateway.on(ReadyEvent.class).subscribe(e -> System.out.println("Logged in as " + e.getSelf().getUsername()));
        gateway.on(MessageCreateEvent.class).subscribe(dispatcher::onMessage);
        gateway.onDisconnect().block();
    }

    private void loadSettings() throws IOException, BadSettingsException {
        // Check if settings file exists
        if (!getSettingsFile().exists()) {
            // Write new settings file
            try (FileWriter fw = new FileWriter(getSettingsFile())) {
                settings.store(fw);
                throw new BadSettingsException("Created new settings file (" + getSettingsFile().getName() + ")");
            }
        } else {
            // Load settings file
            try (FileReader fr = new FileReader(getSettingsFile())) {
                settings.load(fr);
                // Verify settings
                if (settings.get(Settings.TOKEN).isEmpty())
                    throw new BadSettingsException("Token cannot be empty");
                if (settings.get(Settings.PREFIX).isEmpty())
                    throw new BadSettingsException("Prefix cannot be empty");
            }
        }
    }

    public Settings getSettings() {
        return settings;
    }

    public GatewayDiscordClient getGateway() {
        return gateway;
    }

    public CommandDispatcher getDispatcher() {
        return dispatcher;
    }
}

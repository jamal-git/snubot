package com.oopsjpeg.snubot.command;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;

import java.util.Arrays;

public class CommandDispatcher {
    private final String prefix;
    private final CommandRegistry registry;

    public CommandDispatcher(String prefix) {
        this.prefix = prefix.toLowerCase();
        registry = new CommandRegistry();
    }

    public void onMessage(MessageCreateEvent event) {
        Message message = event.getMessage();
        MessageChannel channel = message.getChannel().block();
        User author = message.getAuthor().orElse(null);
        String content = message.getContent();

        if (author != null && channel != null && !author.isBot() && content.toLowerCase().startsWith(prefix)) {
            String[] split = content.split(" ");
            String alias = split[0].replaceFirst(prefix, "");
            String[] args = Arrays.copyOfRange(split, 1, split.length);
            Command command = registry.find(alias);

            if (command != null)
                command.execute(message, alias, args);
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public CommandRegistry getRegistry() {
        return registry;
    }
}

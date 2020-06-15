package com.oopsjpeg.snubot.command;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.PrivateChannel;
import discord4j.rest.util.Color;

import java.util.stream.Collectors;

public enum CommandEnum implements Command {
    HELP("help", "?") {
        @Override
        public void execute(Message message, String alias, String[] args) {
            User self = getBot().getGateway().getSelf().block();
            User author = message.getAuthor().get();
            MessageChannel channel = message.getChannel().block();
            PrivateChannel dm = author.getPrivateChannel().block();

            // Send help embed to direct messages
            dm.createEmbed(e -> {
                e.setAuthor("Snubot Commands", null, self.getAvatarUrl());
                e.setDescription(getBot().getDispatcher().getRegistry().getCommands().stream()
                        .map(c -> "`" + c.getAliases()[0] + "`: " + (c.hasDescription() ? c.getDescription() : ""))
                        .collect(Collectors.joining("\n")));
                e.setColor(Color.ORANGE);
            }).block();

            if (!channel.getId().equals(dm.getId()))
                channel.createMessage(":mailbox_with_mail: " + author.getMention() + " Help is on the way! Check your messages.").block();
        }

        @Override
        public String getDescription() {
            return "Sends a list of commands to DMs.";
        }
    },
    HELLO("hello") {
        @Override
        public void execute(Message message, String alias, String[] args) {
            MessageChannel channel = message.getChannel().block();
            User author = message.getAuthor().get();
            channel.createMessage("Hello, " + author.getMention() + "!").block();
        }

        @Override
        public String getDescription() {
            return "Says hello!";
        }
    };

    private final String[] aliases;

    CommandEnum(String... aliases) {
        this.aliases = aliases;
    }

    @Override
    public String[] getAliases() {
        return aliases;
    }
}

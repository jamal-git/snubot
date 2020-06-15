package com.oopsjpeg.snubot.command;

import com.oopsjpeg.snubot.Snubot;
import discord4j.core.object.entity.Message;

public interface Command {
    void execute(Message message, String alias, String[] args);

    String[] getAliases();

    default String getDescription() {
        return null;
    }

    default boolean hasDescription() {
        return getDescription() != null && !getDescription().isEmpty();
    }

    default Snubot getBot() {
        return Snubot.getInstance();
    }
}

package com.oopsjpeg.snubot.command;

import com.oopsjpeg.snubot.Snubot;
import com.oopsjpeg.snubot.command.exception.CommandException;
import discord4j.core.object.entity.Message;
import discord4j.rest.util.PermissionSet;

public interface Command
{
    void execute(Message message, String alias, String[] args, CommandRegistry registry, Snubot bot) throws CommandException;

    String[] getAliases();

    default String getDescription()
    {
        return null;
    }

    default boolean hasDescription()
    {
        return getDescription() != null && !getDescription().isEmpty();
    }

    default PermissionSet getPermissions()
    {
        return null;
    }

    default boolean hasPermissions()
    {
        return getPermissions() != null;
    }

    default boolean isDeveloperOnly()
    {
        return false;
    }

    default boolean isGuildOnly()
    {
        return isModOnly();
    }

    default boolean isModOnly()
    {
        return false;
    }
}

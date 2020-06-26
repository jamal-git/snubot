package com.oopsjpeg.snubot.command;

import com.oopsjpeg.snubot.Snubot;
import discord4j.core.object.entity.Message;
import discord4j.rest.util.PermissionSet;

public interface Command
{
    void execute(CommandListener parent, Message message, String alias, String[] args) throws CommandException;

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
        return PermissionSet.none();
    }

    default boolean isGuildOnly()
    {
        return false;
    }

    default Snubot getBot()
    {
        return Snubot.getInstance();
    }
}

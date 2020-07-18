package com.oopsjpeg.snubot.command;

import com.oopsjpeg.snubot.Snubot;
import com.oopsjpeg.snubot.command.exception.CommandException;
import com.oopsjpeg.snubot.command.exception.DeveloperOnlyException;
import com.oopsjpeg.snubot.command.exception.GuildOnlyException;
import com.oopsjpeg.snubot.command.exception.PermissionException;
import com.oopsjpeg.snubot.util.Util;
import discord4j.core.object.entity.ApplicationInfo;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.rest.util.PermissionSet;

public interface Command
{
    void execute(Message message, String alias, String[] args, CommandRegistry registry, Snubot bot) throws CommandException;

    default void tryExecute(Message message, String alias, String[] args, CommandRegistry registry, Snubot bot) throws CommandException
    {
        User author = message.getAuthor().get();
        ApplicationInfo appInfo = message.getClient().getApplicationInfo().block();
        MessageChannel channel = message.getChannel().block();

        // Check if command is developer only
        if (isDeveloperOnly() && !author.getId().equals(appInfo.getOwnerId()))
            throw new DeveloperOnlyException();
        // Check if command is guild only
        if (isGuildOnly())
        {
            if (!(channel instanceof TextChannel))
                throw new GuildOnlyException();

            // Check user permissions
            if (hasPermissions() && !Util.hasPermissions((TextChannel) channel, author.getId(), getPermissions()))
                throw new PermissionException();
        }

        execute(message, alias, args, registry, bot);
    }

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
        return false;
    }
}

package com.oopsjpeg.snubot.command;

import com.oopsjpeg.snubot.command.exception.CommandException;
import com.oopsjpeg.snubot.util.Util;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.channel.TextChannel;

public class CommandUtil
{
    public static Role tryRole(Guild g, String s) throws CommandException
    {
        Role role = g.getRoles().filter(r -> s.equals(r.getId().asString()) // ID
                || s.equals(r.getMention()) // Mention
                || Util.searchString(r.getName(), s)) // Name
                .blockFirst();
        if (role == null)
            throw new CommandException("Invalid role specified.");
        return role;
    }

    public static TextChannel tryChannel(Guild g, String s) throws CommandException
    {
        TextChannel channel = g.getChannels().ofType(TextChannel.class)
                .filter(c -> s.equals(c.getId().asString()) // ID
                        || s.equals(c.getMention()) // Mention
                        || Util.searchString(c.getName(), s)) // Name
                .blockFirst();
        if (channel == null)
            throw new CommandException("Invalid channel specified.");
        return channel;
    }

    public static int tryInt(String s, String type) throws CommandException
    {
        try
        {
            return Integer.parseInt(s);
        }
        catch (NumberFormatException ignored)
        {
            throw new CommandException("Invalid " + type + " specified.");
        }
    }

    public static int tryInt(String s, String type, int min, int max) throws CommandException
    {
        int i = tryInt(s, type);
        if (i < min)
            throw new CommandException("Invalid " + type + " specified. Value cannot be less than " + min + ".");
        if (i > max)
            throw new CommandException("Invalid " + type + " specified. Value cannot be more than " + max + ".");
        return i;
    }

    public static int tryIntMin(String s, String type, int min) throws CommandException
    {
        return tryInt(s, type, min, Integer.MAX_VALUE);
    }

    public static int tryIntMax(String s, String type, int max) throws CommandException
    {
        return tryInt(s, type, Integer.MIN_VALUE, max);
    }
}

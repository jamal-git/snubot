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
                .filter(r -> s.equals(r.getId().asString()) // ID
                        || s.equals(r.getMention()) // Mention
                        || Util.searchString(r.getName(), s)) // Name
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
        try
        {
            int i = Integer.parseInt(s);
            if (i < min || i >= max)
                throw new NumberFormatException();
            return i;
        }
        catch (NumberFormatException ignored)
        {
            throw new CommandException("Invalid " + type + " specified.");
        }
    }
}

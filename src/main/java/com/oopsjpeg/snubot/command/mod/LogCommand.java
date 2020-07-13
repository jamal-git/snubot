package com.oopsjpeg.snubot.command.mod;

import com.oopsjpeg.snubot.Snubot;
import com.oopsjpeg.snubot.command.Command;
import com.oopsjpeg.snubot.command.CommandRegistry;
import com.oopsjpeg.snubot.command.CommandUtil;
import com.oopsjpeg.snubot.command.exception.CommandException;
import com.oopsjpeg.snubot.command.exception.InvalidUsageException;
import com.oopsjpeg.snubot.data.impl.GuildData;
import com.oopsjpeg.snubot.util.ChatUtil;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

public class LogCommand implements Command
{

    @Override
    public void execute(Message message, String alias, String[] args, CommandRegistry registry, Snubot bot) throws CommandException
    {
        User author = message.getAuthor().get();
        MessageChannel channel = message.getChannel().block();
        Guild guild = message.getGuild().block();

        // View the current log channel role
        if (args.length == 0)
        {
            GuildData data = bot.getGuildData(guild);
            if (data == null || !data.hasLogChannel())
                throw new InvalidUsageException(this, registry, "<channel>");

            channel.createEmbed(ChatUtil.info(author, "The current log channel is **" + data.getLogChannel().block().getName() + "**.")).block();
        }
        // Set the moderator role
        else
        {
            TextChannel logChannel = CommandUtil.tryChannel(guild, String.join(" ", args));

            GuildData data = bot.getOrAddGuildData(guild);
            data.setLogChannel(logChannel);
            data.markForSave();

            channel.createEmbed(ChatUtil.success(author, "Set the log channel to **" + logChannel.getName() + "**.")).block();
        }
    }

    @Override
    public String[] getAliases()
    {
        return new String[]{"log"};
    }

    @Override
    public String getDescription()
    {
        return "Set the log channel.";
    }

    @Override
    public boolean isGuildOnly()
    {
        return true;
    }

    @Override
    public PermissionSet getPermissions()
    {
        return PermissionSet.of(Permission.MANAGE_CHANNELS);
    }
}

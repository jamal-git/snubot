package com.oopsjpeg.snubot.command.impl.mod;

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
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

public class ModRoleCommand implements Command
{
    @Override
    public void execute(Message message, String alias, String[] args, CommandRegistry registry, Snubot bot) throws CommandException
    {
        User author = message.getAuthor().get();
        MessageChannel channel = message.getChannel().block();
        Guild guild = message.getGuild().block();

        // View the current moderator role
        if (args.length == 0)
        {
            GuildData data = bot.getGuildData(guild);
            if (data == null || !data.hasModRole())
                throw new InvalidUsageException(this, registry, "<role>");

            channel.createEmbed(ChatUtil.info(author, "The current moderator role is **" + data.getModRole().block().getName() + "**.")).block();
        }
        // Set the moderator role
        else
        {
            Role role = CommandUtil.tryRole(guild, String.join(" ", args));

            GuildData data = bot.getOrAddGuildData(guild);
            data.setModRole(role);
            data.markForSave();

            channel.createEmbed(ChatUtil.success(author, "Set the moderator role to **" + role.getName() + "**.")).block();
        }
    }

    @Override
    public String[] getAliases()
    {
        return new String[]{"modrole"};
    }

    @Override
    public String getDescription()
    {
        return "Set the moderator role in the current server.";
    }

    @Override
    public PermissionSet getPermissions()
    {
        return PermissionSet.of(Permission.ADMINISTRATOR);
    }

    @Override
    public boolean isGuildOnly()
    {
        return true;
    }
}

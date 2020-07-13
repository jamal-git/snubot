package com.oopsjpeg.snubot.command.impl.general;

import com.oopsjpeg.snubot.Snubot;
import com.oopsjpeg.snubot.command.Command;
import com.oopsjpeg.snubot.command.CommandRegistry;
import com.oopsjpeg.snubot.command.exception.CommandException;
import com.oopsjpeg.snubot.command.exception.InvalidUsageException;
import com.oopsjpeg.snubot.command.exception.PermissionException;
import com.oopsjpeg.snubot.data.impl.GuildData;
import com.oopsjpeg.snubot.data.impl.MemberData;
import com.oopsjpeg.snubot.util.ChatUtil;
import com.oopsjpeg.snubot.util.Util;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

import java.util.stream.Collectors;

import static com.oopsjpeg.snubot.command.CommandUtil.tryInt;
import static com.oopsjpeg.snubot.command.CommandUtil.tryRole;

public class ColorCommand implements Command
{
    @Override
    public void execute(Message message, String alias, String[] args, CommandRegistry registry, Snubot bot) throws CommandException
    {
        TextChannel channel = message.getChannel().ofType(TextChannel.class).block();
        Guild guild = channel.getGuild().block();
        User author = message.getAuthor().orElse(null);
        GuildData data = bot.getGuildData(guild);

        if (args.length == 0)
        {
            if (data.getColoring().getRoleSet().isEmpty())
                throw new CommandException("There are no colors available.");

            String colors = data.getColoring().getRoleSet().stream().map(r -> '`' + r.getName() + '`').collect(Collectors.joining(", "));
            channel.createEmbed(ChatUtil.info(author, "Available colors: " + colors + ".\n\nSet your color with `" + registry.format(this) + " <color>`.")).block();
        }
        else if (args[0].equals("add"))
        {
            tryEditPerms(channel, author);

            if (args.length < 2)
                throw new InvalidUsageException(this, registry, "add <role>");

            Role role = tryRole(guild, args[1]);
            if (data.getColoring().hasRole(role))
                throw new CommandException("That role is already added.");

            data.getColoring().addRole(role);
            data.markForSave();

            channel.createEmbed(ChatUtil.success(author, "Added **" + role.getName() + "** to colors.")).block();
        }
        else if (args[0].equals("remove"))
        {
            tryEditPerms(channel, author);

            if (args.length < 2)
                throw new InvalidUsageException(this, registry, "remove <role>");

            Role role = tryRole(guild, args[1]);
            if (!data.getColoring().hasRole(role))
                throw new CommandException("That role is not added.");

            data.getColoring().removeRole(role);
            data.markForSave();

            channel.createEmbed(ChatUtil.success(author, "Removed **" + role.getName() + "** from colors.")).block();
        }
        else if (args[0].equals("clear"))
        {
            tryEditPerms(channel, author);

            data.getColoring().clearRoles();
            data.markForSave();

            channel.createEmbed(ChatUtil.success(author, "Remove all roles from colors.")).block();
        }
        else if (args[0].equals("level"))
        {
            tryEditPerms(channel, author);

            if (args.length < 2)
            {
                if (!data.getColoring().hasLevelRequired())
                    throw new InvalidUsageException(this, registry, "level <level>");
                channel.createEmbed(ChatUtil.info(author, "The level requirement for colors is **" + (data.getColoring().getLevelRequired() + 1) + "**.")).block();
            }
            else
            {
                int level = args[1].equalsIgnoreCase("none") ? 0 : tryInt(args[1], "level", 1, 999) - 1;

                data.getColoring().setLevelRequired(level);
                data.markForSave();

                if (level == 0)
                    channel.createEmbed(ChatUtil.success(author, "Disabled level requirement for colors.")).block();
                else
                    channel.createEmbed(ChatUtil.success(author, "Set level requirement for colors to **" + (level + 1) + "**.")).block();
            }
        }
        else
        {
            MemberData memberData = data.getOrAddMemberData(author);
            if (memberData.getLevel() < data.getColoring().getLevelRequired())
                throw new CommandException("You need to be at least level **" + (data.getColoring().getLevelRequired() + 1) + "** to set your color.");
            if (args.length < 1)
                throw new InvalidUsageException(this, registry, "<color>");

            Role role = data.getColoring().getRoleSet().stream()
                    .filter(r -> r.getName().equalsIgnoreCase(args[0])) // Name
                    .findAny().orElse(null);
            if (role == null)
                throw new CommandException("Invalid role specified.");

            data.getColoring().setUserColor(author, role);
            data.markForSave();

            channel.createEmbed(ChatUtil.success(author, "Set color to **" + role.getName() + "**.")).block();
        }
    }

    private void tryEditPerms(TextChannel channel, User user) throws PermissionException
    {
        if (!Util.hasPermissions(channel, user.getId(), PermissionSet.of(Permission.MANAGE_ROLES)))
            throw new PermissionException();
    }

    @Override
    public String[] getAliases()
    {
        return new String[]{"color", "colour"};
    }

    @Override
    public String getDescription()
    {
        return "Set your color in the current server.";
    }

    @Override
    public boolean isGuildOnly()
    {
        return true;
    }
}

package com.oopsjpeg.snubot.command.general;

import com.oopsjpeg.snubot.Snubot;
import com.oopsjpeg.snubot.command.Command;
import com.oopsjpeg.snubot.command.CommandRegistry;
import com.oopsjpeg.snubot.command.exception.CommandException;
import com.oopsjpeg.snubot.command.exception.InvalidUsageException;
import com.oopsjpeg.snubot.command.exception.PermissionException;
import com.oopsjpeg.snubot.data.impl.GuildData;
import com.oopsjpeg.snubot.data.impl.LevelRole;
import com.oopsjpeg.snubot.data.impl.MemberData;
import com.oopsjpeg.snubot.util.ChatUtil;
import com.oopsjpeg.snubot.util.Util;
import discord4j.core.object.entity.*;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

import java.util.Comparator;
import java.util.stream.Collectors;

import static com.oopsjpeg.snubot.command.CommandUtil.tryInt;
import static com.oopsjpeg.snubot.command.CommandUtil.tryRole;

public class LevelCommand implements Command
{
    @Override
    public void execute(Message message, String alias, String[] args, CommandRegistry registry, Snubot bot) throws CommandException
    {
        User author = message.getAuthor().get();
        TextChannel channel = message.getChannel().cast(TextChannel.class).block();
        Guild guild = message.getGuild().block();

        // Show level in the current server
        if (args.length == 0)
        {
            GuildData guildData = bot.getOrAddGuildData(guild);
            MemberData memberData = guildData.getOrAddMemberData(author);

            channel.createEmbed(ChatUtil.info(author, "Level **" + (memberData.getLevel() + 1) + "** (" + memberData.getXp() + " / " + memberData.getMaxXp() + ")")).block();
        }
        // Perform a level command
        else
        {
            // Add a level-based role
            if (args[0].equalsIgnoreCase("addrole"))
            {
                tryEditPerms(channel, author);

                if (args.length < 3)
                    throw new InvalidUsageException(this, registry, "addrole <role> <level>");

                GuildData guildData = bot.getOrAddGuildData(guild);
                Role role = tryRole(guild, args[1]);
                int level = tryInt(args[2], "level", 1, guildData.getLeveling().getMaxLevel());

                guildData.getLeveling().addRole(role, level);
                guildData.markForSave();

                channel.createEmbed(ChatUtil.success(author, "Added **" + role.getName() + "** to level **" + level + "** in **" + guild.getName() + "**.")).block();
            }
            // Remove a level-based role
            else if (args[0].equalsIgnoreCase("removerole"))
            {
                tryEditPerms(channel, author);

                if (args.length < 2)
                    throw new InvalidUsageException(this, registry, "removerole <role>");

                GuildData guildData = bot.getOrAddGuildData(guild);
                tryRolesNotEmpty(guild, guildData);
                Role role = tryRole(guild, args[1]);

                if (!guildData.getLeveling().hasRole(role))
                    throw new CommandException("**" + role.getName() + "** is not set to any level.");

                guildData.getLeveling().removeRole(role);
                guildData.markForSave();

                channel.createEmbed(ChatUtil.success(author, "Removed **" + role.getName() + "** from level-based roles in **" + guild.getName() + "**.")).block();
            }
            // List all level-based roles
            else if (args[0].equalsIgnoreCase("list"))
            {
                GuildData guildData = bot.getOrAddGuildData(guild);
                tryRolesNotEmpty(guild, guildData);

                channel.createEmbed(ChatUtil.info(author, guildData.getLeveling().getRoleMap().values().stream()
                        .sorted(Comparator.comparingInt(LevelRole::getLevel))
                        .map(i -> "Level " + i + ": " + guildData.getLeveling().getRolesForLevel(i.getLevel()).stream()
                                .map(Role::getName)
                                .collect(Collectors.joining(", ")))
                        .collect(Collectors.joining("\n")))).block();
            }
            // Sync the user's level-based roles
            else if (args[0].equalsIgnoreCase("sync"))
            {
                GuildData guildData = bot.getOrAddGuildData(guild);
                tryRolesNotEmpty(guild, guildData);

                guildData.getLeveling().syncRoles(author);

                channel.createEmbed(ChatUtil.success(author, "Synced level-based roles in **" + guild.getName() + "**.")).block();
            }
            // Set the max level
            else if (args[0].equalsIgnoreCase("max"))
            {
                tryEditPerms(channel, author);

                if (args.length < 2)
                {
                    GuildData guildData = bot.getOrAddGuildData(guild);
                    channel.createEmbed(ChatUtil.info(author, "The current max level is **" + guildData.getLeveling().getMaxLevel() + "**.")).block();
                }
                else
                {
                    int max = tryInt(args[1], "maximum", 1, 999);

                    GuildData guildData = bot.getOrAddGuildData(guild);
                    guildData.getLeveling().setMaxLevel(max);
                    guildData.markForSave();

                    channel.createEmbed(ChatUtil.success(author, "Set maximum level to **" + max + "** in **" + guild.getName() + "**.")).block();
                }
            }
            else
                throw new InvalidUsageException(this, registry, "<addrole/removerole/list/sync/max>");
        }
    }

    private void tryEditPerms(TextChannel channel, User user) throws PermissionException
    {
        if (!Util.hasPermissions(channel, user.getId(), PermissionSet.of(Permission.MANAGE_GUILD)))
            throw new PermissionException();
    }

    private void tryRolesNotEmpty(Guild guild, GuildData data) throws CommandException
    {
        if (data == null || data.getLeveling().getRoleMap().isEmpty())
            throw new CommandException("There are no level-based roles in **" + guild.getName() + "**.");
    }

    @Override
    public String[] getAliases()
    {
        return new String[]{"level"};
    }

    @Override
    public String getDescription()
    {
        return "View your level progress in the current server.";
    }

    @Override
    public boolean isGuildOnly()
    {
        return true;
    }
}

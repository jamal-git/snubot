package com.oopsjpeg.snubot.command.impl.mod;

import com.oopsjpeg.snubot.command.Command;
import com.oopsjpeg.snubot.util.ChatUtil;
import com.oopsjpeg.snubot.command.CommandRegistry;
import com.oopsjpeg.snubot.command.CommandUtil;
import com.oopsjpeg.snubot.util.Util;
import com.oopsjpeg.snubot.command.exception.CommandException;
import com.oopsjpeg.snubot.command.exception.InvalidUsageException;
import com.oopsjpeg.snubot.Snubot;
import com.oopsjpeg.snubot.data.impl.Selections;
import com.oopsjpeg.snubot.data.impl.UserData;
import com.oopsjpeg.snubot.react.ReactManager;
import com.oopsjpeg.snubot.react.ReactMessage;
import com.oopsjpeg.snubot.react.ReactRole;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;

public class ReactIonRolesCommand implements Command
{
    @Override
    public void execute(Message message, String alias, String[] args, CommandRegistry registry, Snubot bot) throws CommandException
    {
        MessageChannel channel = message.getChannel().block();
        User author = message.getAuthor().get();
        UserData data = bot.getOrAddUserData(author.getId().asString());
        Guild guild = message.getGuild().block();
        ReactManager manager = bot.getReactManager();

        // Show information about selected message
        if (args.length == 0)
        {
            Message selectedMessage = trySelectedMessage(data.getSelections(), registry, bot.getGateway()).block();

            String content = "Editing [selected message](" + data.getSelections().getMessageUrl() + ") by " + ChatUtil.formatUser(selectedMessage.getAuthor().get()) + ".";
            if (manager.has(selectedMessage))
            {
                ReactMessage reactMessage = manager.get(selectedMessage);
                content += "\n" + reactMessage.getEmojiCount() + " emoji(s) linked to " + reactMessage.getRoleCount() + " role(s).";
            }
            channel.createEmbed(ChatUtil.info(author, content)).block();
        }
        // Perform a reaction roles command
        else
        {
            // Select a message to edit reaction-based roles on
            if (args[0].equalsIgnoreCase("select"))
            {
                if (args.length < 3)
                    throw new InvalidUsageException(this, registry, "select <channel> <message id>");

                TextChannel selectedChannel = CommandUtil.tryChannel(guild, args[1]);
                Message selectedMessage = tryMessage(selectedChannel, args[2]);

                data.getSelections().setMessage(selectedMessage);
                data.markForSave();

                channel.createEmbed(ChatUtil.success(author, "Selected [specified message](" + data.getSelections().getMessageUrl() + ") by " + ChatUtil.formatUser(selectedMessage.getAuthor().get()) + " in " + selectedChannel.getMention() + ".")).block();
            }
            // Add a reaction-based role
            else if (args[0].equalsIgnoreCase("add"))
            {
                if (args.length < 3)
                    throw new InvalidUsageException(this, registry, "add <role> <emoji> [mode]");

                Message selectedMessage = trySelectedMessage(data.getSelections(), registry, bot.getGateway()).block();
                Role role = CommandUtil.tryRole(guild, args[1]);
                ReactionEmoji emoji = Util.stringToEmoji(args[2]);

                // Test if the emoji is real by reacting
                AtomicBoolean sentReaction = new AtomicBoolean(true);
                message.addReaction(emoji)
                        .doOnError(e -> sentReaction.set(false))
                        .onErrorResume(e -> Mono.empty())
                        .block();
                if (!sentReaction.get())
                    throw new CommandException("Invalid emoji specified.");

                ReactRole.Type type = args.length >= 4 ? ReactRole.Type.fromName(args[3]) : ReactRole.Type.TOGGLE;
                if (type == null)
                    throw new CommandException("Invalid role type specified.");

                ReactMessage reactMessage = manager.getOrAdd(selectedMessage);
                manager.addRoleToEmoji(reactMessage, emoji, role, type);
                reactMessage.markForSave();

                channel.createEmbed(ChatUtil.success(author, "Added **" + role.getName() + "** (" + type.getName() + ") to " + Util.emojiToString(emoji) + " on [selected message](" + data.getSelections().getMessageUrl() + ").")).block();
            }
            // Remove a reaction-based role
            else if (args[0].equalsIgnoreCase("remove"))
            {
                if (args.length < 2)
                    throw new InvalidUsageException(this, registry, "remove <role>");

                Message selectedMessage = trySelectedMessage(data.getSelections(), registry, bot.getGateway()).block();
                ReactMessage reactMessage = tryReactMessage(manager, selectedMessage);
                Role role = CommandUtil.tryRole(guild, args[1]);

                manager.removeRole(reactMessage, role);
                reactMessage.markForSave();

                channel.createEmbed(ChatUtil.success(author, "Removed **" + role.getName() + "** from all emojis on [selected message](" + data.getSelections().getMessageUrl() + ").")).block();
            }
            // Clear all reaction-based roles
            else if (args[0].equalsIgnoreCase("clear"))
            {
                Message selectedMessage = trySelectedMessage(data.getSelections(), registry, bot.getGateway()).block();

                manager.remove(selectedMessage);

                channel.createEmbed(ChatUtil.success(author, "Removed all reaction-based roles from [selected message](" + data.getSelections().getMessageUrl() + ").")).block();
            }
            else throw new InvalidUsageException(this, registry, "<select/add/remove/clear>");
        }
    }

    private Mono<Message> trySelectedMessage(Selections s, CommandRegistry r, GatewayDiscordClient gateway) throws CommandException
    {
        if (!s.hasMessage())
            throw new CommandException("Select a message first with `" + r.format(this) + " select <channel> <message id>` to edit reaction-based roles.");
        return s.getMessage();
    }

    private Message tryMessage(TextChannel c, String s) throws CommandException
    {
        Message message = c.getMessageById(Snowflake.of(s)).onErrorResume(error -> Mono.empty()).block();
        if (message == null)
            throw new CommandException("Invalid message ID specified. Is the message in " + c.getMention() + "?");
        return message;
    }

    private ReactMessage tryReactMessage(ReactManager r, Message m) throws CommandException
    {
        if (!r.has(m))
            throw new CommandException("There are no reaction-based roles on this message.");
        return r.get(m);
    }

    @Override
    public String[] getAliases()
    {
        return new String[]{"rr", "reactionroles"};
    }

    @Override
    public String getDescription()
    {
        return "Edit reaction-based roles.";
    }

    @Override
    public PermissionSet getPermissions()
    {
        return PermissionSet.of(Permission.MANAGE_ROLES);
    }

    @Override
    public boolean isGuildOnly()
    {
        return true;
    }
}

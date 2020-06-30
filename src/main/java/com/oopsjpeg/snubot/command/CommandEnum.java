package com.oopsjpeg.snubot.command;

import com.oopsjpeg.snubot.data.GuildData;
import com.oopsjpeg.snubot.data.MemberData;
import com.oopsjpeg.snubot.data.UserData;
import com.oopsjpeg.snubot.react.ReactContainer;
import com.oopsjpeg.snubot.react.ReactManager;
import com.oopsjpeg.snubot.react.ReactRole;
import com.oopsjpeg.snubot.util.PagedList;
import com.oopsjpeg.snubot.util.Util;
import discord4j.common.util.Snowflake;
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

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public enum CommandEnum implements Command
{
    HELP("help", "?")
            {
                @Override
                public void execute(CommandListener parent, Message message, String alias, String[] args) throws CommandException
                {
                    User self = getBot().getGateway().getSelf().block();
                    User author = message.getAuthor().get();
                    MessageChannel channel = message.getChannel().block();
                    String search = String.join(" ", args);

                    // Show all commands if search is empty/page
                    if (search.isEmpty() || Util.isDigits(search))
                    {
                        // Create a paged list of commands
                        PagedList<Command> commands = new PagedList<>(parent.getCommandSet(), 15);
                        int page = search.isEmpty() ? 0 : Integer.parseInt(args[0]) - 1;

                        // Handle invalid pages
                        if (page < 0 || page >= commands.pages())
                            throw new CommandException("Invalid page (" + commands.pages() + " page(s)).");

                        // Send embed
                        channel.createEmbed(Util.embed(author).andThen(e ->
                        {
                            e.setTitle("Commands");
                            e.setDescription(commands.format(page, c -> "`" + c.getAliases()[0] + "`: " + c.getDescription()));
                            e.setFooter("Page " + (page + 1) + " / " + commands.pages(), null);
                        })).block();
                    }
                    // Show specified command
                    else
                    {
                        Command command = parent.find(search);

                        if (command == null)
                            throw new CommandException("Invalid command name.");

                        // Send embed
                        channel.createEmbed(Util.embed(author).andThen(e ->
                        {
                            e.setTitle(command.getAliases()[0]);

                            String description = "";
                            if (command.hasDescription())
                                description += command.getDescription() + "\n\n";
                            String aliases = Arrays.stream(command.getAliases()).map(a -> '`' + a + '`').collect(Collectors.joining(", "));
                            description += "Aliases: [" + aliases + "]";

                            e.setDescription(description);
                        })).block();
                    }
                }

                @Override
                public String getDescription()
                {
                    return "Send a list of commands to your direct messages.";
                }
            },
    SELECT("select")
            {
                @Override
                public void execute(CommandListener parent, Message message, String alias, String[] args) throws CommandException
                {
                    if (args.length < 2)
                        throw new CommandException("Correct usage: `" + parent.format(this) + " <#channel> <message id>`");

                    User author = message.getAuthor().get();
                    UserData data = getBot().getOrAddUserData(author.getId().asString());
                    MessageChannel channel = message.getChannel().block();
                    Guild guild = message.getGuild().block();

                    TextChannel targetChannel = guild.getChannels().ofType(TextChannel.class)
                            .filter(c -> args[0].equals(c.getMention()))
                            .blockFirst();
                    if (targetChannel == null)
                        throw new CommandException("Invalid channel specified.");

                    Message targetMessage = targetChannel.getMessageById(Snowflake.of(args[1]))
                            .onErrorResume(error -> Mono.empty()).block();
                    if (targetMessage == null)
                        throw new CommandException("Invalid message ID specified. Is it in " + targetChannel.getMention() + "?");

                    data.getSelections().setGuildId(guild.getId().asString());
                    data.getSelections().setChannelId(targetChannel.getId().asString());
                    data.getSelections().setMessageId(targetMessage.getId().asString());

                    Util.send(channel, author, "Selected [specified message](" + data.getSelections().getMessageLink() + ") by "
                            + Util.formatUser(targetMessage.getAuthor().get()) + " in " + targetChannel.getMention() + ".");
                }

                @Override
                public String getDescription()
                {
                    return "Select a message to use with reaction roles.";
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
            },
    REACTION_ROLES("rr")
            {
                @Override
                public void execute(CommandListener parent, Message message, String alias, String[] args) throws CommandException
                {
                    MessageChannel channel = message.getChannel().block();
                    User author = message.getAuthor().get();
                    UserData data = getBot().getOrAddUserData(author.getId().asString());
                    ReactManager manager = getBot().getReactManager();

                    if (!data.getSelections().hasMessage())
                        throw new CommandException("Select a message with `" + parent.format(SELECT) + "` first.");

                    Message selectedMessage = data.getSelections().getMessage().block();

                    if (args.length == 0)
                    {
                        String content = "Editing [message](" + data.getSelections().getMessageLink() + ") by " + Util.formatUser(selectedMessage.getAuthor().get()) + ".";
                        if (manager.hasContainer(selectedMessage))
                        {
                            ReactContainer container = manager.getContainer(selectedMessage);
                            content += "\n" + container.getReactionCount() + " emoji(s) linked to " + container.getRoleCount() + " role(s).";
                        }
                        content += "\n\nAdd a role: `" + parent.format(this) + " add <role> <emoji> [mode]`";
                        content += "\nRemove a role: `" + parent.format(this) + " remove <role>`";
                        Util.send(channel, author, content);
                    }
                    else
                    {
                        Guild guild = selectedMessage.getGuild().block();

                        if (args[0].equalsIgnoreCase("add"))
                        {
                            if (args.length < 3)
                                throw new CommandException("Correct usage: `" + parent.format(this) + " add <role> <emoji> [mode]`");

                            Role role = searchRole(guild, args[1]);
                            if (role == null)
                                throw new CommandException("Invalid role specified.");

                            ReactionEmoji emoji = Util.stringToEmoji(args[2]);
                            // Test if the emoji is real by reacting
                            AtomicBoolean sentReaction = new AtomicBoolean(true);
                            message.addReaction(emoji)
                                    .doOnError(e -> sentReaction.set(false))
                                    .onErrorResume(e -> Mono.empty())
                                    .block();
                            if (!sentReaction.get())
                                throw new CommandException("Invalid emoji specified. It may not be supported yet.");

                            ReactRole.Type type = args.length >= 4 ? ReactRole.Type.fromName(args[3]) : ReactRole.Type.TOGGLE;
                            if (type == null)
                                throw new CommandException("Invalid role type specified.");

                            manager.addRoleToEmoji(selectedMessage, emoji, role, type);

                            Util.send(channel, author, "Added **" + role.getName() + "** (" + type.getName() + ") to " + Util.emojiToString(emoji) + " on [message](" + data.getSelections().getMessageLink() + ").");
                        }
                        else if (args[0].equalsIgnoreCase("remove"))
                        {
                            if (args.length < 2)
                                throw new CommandException("Correct usage: `" + parent.format(this) + " remove <role>`");
                            if (!manager.hasContainer(selectedMessage))
                                throw new CommandException("There are no reaction roles on this message.");

                            Role role = searchRole(guild, args[1]);
                            if (role == null)
                                throw new CommandException("Invalid role specified.");

                            manager.removeRoleFromAll(selectedMessage, role);

                            Util.send(channel, author, "Removed **" + role.getName() + "** from all reactions on [message](" + data.getSelections().getMessageLink() + ").");
                        }
                        else if (args[0].equalsIgnoreCase("update"))
                        {
                            if (!manager.hasContainer(selectedMessage))
                                throw new CommandException("There are no reaction roles on this message.");
                            manager.updateContainer(selectedMessage);
                            Util.send(channel, author, "Updated [message](" + data.getSelections().getMessageLink() + ").");
                        }
                        else throw new CommandException("Invalid edit command.");
                    }
                }

                private Role searchRole(Guild g, String s)
                {
                    return g.getRoles().filter(r -> s.equals(r.getId().asString()) // ID
                            || s.equals(r.getMention()) // Mention
                            || Util.searchString(r.getName(), s)).blockFirst(); // Name
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
            },
    LEVEL("level")
            {
                @Override
                public void execute(CommandListener parent, Message message, String alias, String[] args) throws CommandException
                {
                    User author = message.getAuthor().get();
                    TextChannel channel = message.getChannel().cast(TextChannel.class).block();
                    Guild guild = message.getGuild().block();

                    if (args.length == 0)
                    {
                        GuildData guildData = getBot().getGuildData(guild.getId().asString());

                        if (guildData == null || !guildData.isLevelingEnabled())
                            throw new CommandException("User leveling is not enabled in this server.");

                        MemberData memberData = guildData.getOrAddMemberData(author.getId().asString());

                        Util.send(channel, author, "Level **" + (memberData.getLevel() + 1) + "** (" + memberData.getXp() + " / " + memberData.xpRequired() + ")");
                    }
                    else if (args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("disable"))
                    {
                        if (!Util.hasPermissions(channel, author.getId(), PermissionSet.of(Permission.MANAGE_GUILD)))
                            throw new CommandException("You do not have permission to use this command.");

                        GuildData guildData = getBot().getOrAddGuildData(guild.getId().asString());

                        if (args[0].equalsIgnoreCase("enable"))
                        {
                            if (guildData.isLevelingEnabled())
                                throw new CommandException("User leveling is already enabled in **" + guild.getName() + "**.");
                            guildData.setLevelingEnabled(true);
                            Util.send(channel, author, "Enabled user leveling in **" + guild.getName() + "**.");
                        }
                        else if (args[0].equalsIgnoreCase("disable"))
                        {
                            if (!guildData.isLevelingEnabled())
                                throw new CommandException("User leveling is already disabled in **" + guild.getName() + "**.");
                            guildData.setLevelingEnabled(false);
                            Util.send(channel, author, "Disabled user leveling in **" + guild.getName() + "**.");
                        }
                    }
                }

                @Override
                public String getDescription()
                {
                    return "Show your level progress in the current server.";
                }

                @Override
                public boolean isGuildOnly()
                {
                    return true;
                }
            },
    SAVE_ALL("saveall")
            {
                @Override
                public void execute(CommandListener parent, Message message, String alias, String[] args)
                {
                    User author = message.getAuthor().get();
                    MessageChannel channel = message.getChannel().block();
                    getBot().saveAll();
                    Util.send(channel, author, "Saved all data.");
                }

                @Override
                public String getDescription()
                {
                    return "Save all data.";
                }

                @Override
                public boolean isDeveloperOnly()
                {
                    return true;
                }
            };

    private final String[] aliases;

    CommandEnum(String... aliases)
    {
        this.aliases = aliases;
    }

    @Override
    public String[] getAliases()
    {
        return aliases;
    }
}
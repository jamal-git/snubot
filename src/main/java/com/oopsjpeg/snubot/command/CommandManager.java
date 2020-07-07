package com.oopsjpeg.snubot.command;

import com.oopsjpeg.snubot.Snubot;
import com.oopsjpeg.snubot.command.exception.*;
import com.oopsjpeg.snubot.data.impl.GuildData;
import com.oopsjpeg.snubot.manager.Manager;
import com.oopsjpeg.snubot.util.ChatUtil;
import com.oopsjpeg.snubot.util.Util;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.TextChannel;

public class CommandManager implements Manager
{
    private final Snubot parent;
    private final CommandRegistry registry;

    public CommandManager(Snubot parent, CommandRegistry registry)
    {
        this.parent = parent;
        this.registry = registry;
    }

    public void onMessage(MessageCreateEvent event)
    {
        GatewayDiscordClient client = event.getClient();
        Message message = event.getMessage();
        MessageChannel channel = message.getChannel().block();
        User author = message.getAuthor().orElse(null);
        String content = message.getContent();

        if (author != null && channel != null && !author.isBot() && content.toLowerCase().startsWith(registry.getPrefix()))
        {
            // Split the content into command syntax
            String[] split = content.split(" ");
            String alias = split[0].replaceFirst(registry.getPrefix(), "");
            String[] args = Util.buildArguments(content);

            Command command = registry.find(alias);

            if (command != null)
            {
                try
                {
                    // Check if command is developer only
                    if (command.isDeveloperOnly() && !author.getId().equals(client.getApplicationInfo().block().getOwnerId()))
                        throw new DeveloperOnlyException();
                    // Check if command is guild only
                    if (command.isGuildOnly())
                    {
                        if (!(channel instanceof TextChannel))
                            throw new GuildOnlyException();

                        TextChannel textChannel = (TextChannel) channel;
                        Guild guild = textChannel.getGuild().block();

                        // Check if command is moderator only
                        if (command.isModOnly() && getParent().hasGuildData(guild))
                        {
                            GuildData data = getParent().getGuildData(guild);
                            Member member = guild.getMemberById(author.getId()).block();

                            if (!data.hasModRole() || !member.getRoleIds().contains(data.getModRoleIdAsSnowflake()))
                                throw new ModOnlyException();
                        }

                        // Check user permissions
                        if (command.hasPermissions() && !Util.hasPermissions(textChannel, author.getId(), command.getPermissions()))
                            throw new PermissionException();
                    }

                    command.execute(message, alias, args, registry, parent);
                }
                catch (CommandException error)
                {
                    channel.createEmbed(ChatUtil.error(author, error.getMessage())).block();
                }
                catch (Exception error)
                {
                    channel.createEmbed(ChatUtil.error(author, "Unhandled error: `" + error.getMessage() + "`\nContact the developer about this error."));
                    error.printStackTrace();
                }
            }
        }
    }

    @Override
    public Snubot getParent()
    {
        return parent;
    }

    @Override
    public void register(GatewayDiscordClient gateway)
    {
        gateway.on(MessageCreateEvent.class).subscribe(this::onMessage);
    }
}

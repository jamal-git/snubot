package com.oopsjpeg.snubot.command;

import com.oopsjpeg.snubot.Manager;
import com.oopsjpeg.snubot.Snubot;
import com.oopsjpeg.snubot.command.exception.CommandException;
import com.oopsjpeg.snubot.util.ChatUtil;
import com.oopsjpeg.snubot.util.Util;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;

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
                    command.tryExecute(message, alias, args, registry, parent);
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

    public CommandRegistry getRegistry()
    {
        return registry;
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

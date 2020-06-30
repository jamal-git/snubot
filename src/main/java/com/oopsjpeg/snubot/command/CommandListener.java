package com.oopsjpeg.snubot.command;

import com.oopsjpeg.snubot.util.Util;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.TextChannel;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CommandListener
{
    private final String prefix;
    private final Set<Command> commandSet = new HashSet<>();

    public CommandListener(String prefix)
    {
        this.prefix = prefix.toLowerCase();
    }

    public void onMessage(MessageCreateEvent event)
    {
        GatewayDiscordClient client = event.getClient();
        Message message = event.getMessage();
        MessageChannel channel = message.getChannel().block();
        User author = message.getAuthor().orElse(null);
        String content = message.getContent();

        if (author != null && channel != null && !author.isBot() && content.toLowerCase().startsWith(prefix))
        {
            // Split the content into command syntax
            String[] split = content.split(" ");
            String alias = split[0].replaceFirst(prefix, "");
            String[] args = Util.buildArguments(content);

            Command command = find(alias);

            if (command != null)
            {
                try
                {
                    // TODO Turn the following code into exceptions

                    // Check if command is developer only
                    if (command.isDeveloperOnly() && !author.getId().equals(client.getApplicationInfo().block().getOwnerId()))
                        throw new CommandException("This command can only be used by the developer.");
                    // Check if command is guild only
                    if (command.isGuildOnly() && !(channel instanceof TextChannel))
                        throw new CommandException("This command can only be used in servers.");
                    // Check permissions if necessary
                    if (!command.getPermissions().isEmpty() && channel instanceof TextChannel && !Util.hasPermissions((TextChannel) channel, author.getId(), command.getPermissions()))
                        throw new CommandException("You do not have permission to use this command.");

                    command.execute(this, message, alias, args);
                }
                catch (CommandException error)
                {
                    Util.send(channel, author, ":x: " + error.getMessage());
                }
                catch (Exception error)
                {
                    Util.send(channel, author, ":x: Unhandled error: `" + error.getMessage() + "`\nContact the developer about this error.");
                    error.printStackTrace();
                }
            }
        }
    }

    public Command find(String alias)
    {
        return commandSet.stream()
                .filter(c -> Arrays.stream(c.getAliases())
                        .anyMatch(a -> a.equalsIgnoreCase(alias)))
                .findAny().orElse(null);
    }

    public String format(Command command)
    {
        return prefix + command.getAliases()[0];
    }

    public String getPrefix()
    {
        return prefix;
    }

    public Set<Command> getCommandSet()
    {
        return commandSet;
    }
}

package com.oopsjpeg.snubot.command.impl.general;

import com.oopsjpeg.snubot.command.Command;
import com.oopsjpeg.snubot.command.CommandRegistry;
import com.oopsjpeg.snubot.command.CommandUtil;
import com.oopsjpeg.snubot.util.ChatUtil;
import com.oopsjpeg.snubot.util.PagedList;
import com.oopsjpeg.snubot.util.Util;
import com.oopsjpeg.snubot.Snubot;
import com.oopsjpeg.snubot.command.exception.CommandException;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;

import java.util.Arrays;
import java.util.stream.Collectors;

public class HelpCommand implements Command
{
    @Override
    public void execute(Message message, String alias, String[] args, CommandRegistry registry, Snubot bot) throws CommandException
    {
        User author = message.getAuthor().get();
        MessageChannel channel = message.getChannel().block();
        String search = String.join(" ", args);

        // Show all commands if search is empty/page
        if (search.isEmpty() || Util.isDigits(search))
        {
            // Create a paged list of commands
            PagedList<Command> commands = new PagedList<>(registry, 15);
            int page = search.isEmpty() ? 0 : CommandUtil.tryInt(args[0], "page (" + commands.pages() + " page(s))", 1, commands.pages() + 1) - 1;

            // Send embed
            channel.createEmbed(ChatUtil.authorUser(author).andThen(e ->
            {
                e.setTitle("Commands");
                e.setDescription(commands.format(page, c -> "`" + c.getAliases()[0] + "`: " + c.getDescription()));
                e.setFooter("Page " + (page + 1) + " / " + commands.pages(), null);
            })).block();
        }
        // Show specified command
        else
        {
            Command command = registry.find(search);

            if (command == null)
                throw new CommandException("Invalid command name.");

            // Send embed
            channel.createEmbed(ChatUtil.authorUser(author).andThen(e ->
            {
                e.setTitle(command.getAliases()[0]);

                String description = "";
                // Add command description to description
                if (command.hasDescription()) description += command.getDescription() + "\n\n";
                // Add aliases to description
                String aliases = Arrays.stream(command.getAliases()).map(a -> '`' + a + '`').collect(Collectors.joining(", "));
                description += "Aliases: [" + aliases + "]";

                e.setDescription(description);
            })).block();
        }
    }

    @Override
    public String[] getAliases()
    {
        return new String[]{"help", "?"};
    }

    @Override
    public String getDescription()
    {
        return "View helpful information about commands.";
    }
}

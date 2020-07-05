package com.oopsjpeg.snubot.command.dev;

import com.oopsjpeg.snubot.Snubot;
import com.oopsjpeg.snubot.command.Command;
import com.oopsjpeg.snubot.command.CommandRegistry;
import com.oopsjpeg.snubot.util.ChatUtil;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;

public class SaveAllCommand implements Command
{
    @Override
    public void execute(Message message, String alias, String[] args, CommandRegistry registry, Snubot bot)
    {
        User author = message.getAuthor().get();
        MessageChannel channel = message.getChannel().block();
        bot.saveAll();
        channel.createEmbed(ChatUtil.success(author, "Saved all data.")).block();
    }

    @Override
    public String[] getAliases()
    {
        return new String[]{"saveall"};
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
}

package com.oopsjpeg.snubot.command.impl.general;

import com.oopsjpeg.snubot.command.Command;
import com.oopsjpeg.snubot.command.CommandRegistry;
import com.oopsjpeg.snubot.command.exception.CommandException;
import com.oopsjpeg.snubot.Snubot;
import com.oopsjpeg.snubot.data.impl.MemberData;
import com.oopsjpeg.snubot.util.Embeds;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;

public class ProfileCommand implements Command
{
    @Override
    public void execute(Message message, String alias, String[] args, CommandRegistry registry, Snubot bot)
    {
        User author = message.getAuthor().get();
        MessageChannel channel = message.getChannel().block();
        Guild guild = message.getGuild().block();

        MemberData data = bot.getOrAddGuildData(guild).getOrAddMemberData(author);
        channel.createEmbed(Embeds.profile(data)).block();
    }

    @Override
    public String[] getAliases()
    {
        return new String[]{"profile"};
    }

    @Override
    public boolean isGuildOnly()
    {
        return true;
    }
}

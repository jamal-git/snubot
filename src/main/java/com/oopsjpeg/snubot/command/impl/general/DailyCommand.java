package com.oopsjpeg.snubot.command.impl.general;

import com.oopsjpeg.snubot.command.Command;
import com.oopsjpeg.snubot.data.impl.MemberData;
import com.oopsjpeg.snubot.util.ChatUtil;
import com.oopsjpeg.snubot.command.CommandRegistry;
import com.oopsjpeg.snubot.util.Util;
import com.oopsjpeg.snubot.command.exception.CommandException;
import com.oopsjpeg.snubot.Snubot;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;

import java.time.LocalDateTime;

public class DailyCommand implements Command
{
    @Override
    public void execute(Message message, String alias, String[] args, CommandRegistry registry, Snubot bot) throws CommandException
    {
        User author = message.getAuthor().get();
        Guild guild = message.getGuild().block();
        MemberData data = bot.getOrAddGuildData(guild).getOrAddMemberData(author);

        if (!data.hasDaily())
            throw new CommandException("Your **Daily** is available in " + Util.timeDiff(LocalDateTime.now(), data.getLastDailyTime().plusDays(1)) + ".");

        MessageChannel channel = message.getChannel().block();

        int coins = (int) data.getDailyCoins();
        int xp = (int) data.getDailyXp();

        data.daily();
        data.getParent().markForSave();

        channel.createEmbed(ChatUtil.info(author, "Collected **" + Util.comma(coins) + "** coin(s) and **" + Util.comma(xp)
                + "** XP (" + Util.comma(data.getXp()) + " / " + Util.comma(data.getMaxXp()) + ") from **Daily**.")).block();
    }

    @Override
    public String[] getAliases()
    {
        return new String[]{"daily"};
    }

    @Override
    public boolean isGuildOnly()
    {
        return true;
    }
}

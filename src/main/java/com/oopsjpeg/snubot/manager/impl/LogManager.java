package com.oopsjpeg.snubot.manager.impl;

import com.oopsjpeg.snubot.Snubot;
import com.oopsjpeg.snubot.data.impl.GuildData;
import com.oopsjpeg.snubot.manager.Manager;
import com.oopsjpeg.snubot.util.ChatUtil;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageDeleteEvent;
import discord4j.core.event.domain.message.MessageUpdateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;

import java.time.LocalDateTime;
import java.util.function.Consumer;

public class LogManager implements Manager
{
    private final Snubot parent;

    public LogManager(Snubot parent)
    {
        this.parent = parent;
    }

    public void onMessageUpdate(MessageUpdateEvent event)
    {
        Message message = event.getMessage().block();
        MessageChannel channel = message.getChannel().block();
        User author = message.getAuthor().orElse(null);
        Guild guild = message.getGuild().block();

        if (author != null && guild != null && !author.isBot() && event.isContentChanged() && parent.hasGuildData(guild))
        {
            GuildData data = parent.getGuildData(guild);

            if (data.hasLogChannel())
                data.getLogChannel().block().createEmbed(format(author).andThen(e ->
                {
                    String before = event.getOld().get().getContent();

                    e.setColor(Color.CYAN);
                    e.setDescription("**Message edited in " + channel.getMention() + "** ([Jump to Message](" + ChatUtil.url(message) + "))");
                    e.addField("Before", before.isEmpty() ? "None" : before, false);
                    e.addField("After", message.getContent(), false);
                })).block();
        }
    }

    public void onMessageDelete(MessageDeleteEvent event)
    {
        Message message = event.getMessage().get();
        MessageChannel channel = message.getChannel().block();
        User author = message.getAuthor().orElse(null);
        Guild guild = message.getGuild().block();

        if (author != null && guild != null && !author.isBot() && parent.hasGuildData(guild))
        {
            GuildData data = parent.getGuildData(guild);

            if (data.hasLogChannel())
                data.getLogChannel().block().createEmbed(format(author).andThen(e ->
                {
                    e.setColor(Color.RED);
                    e.setDescription("**Message deleted in " + channel.getMention() + "**\n" + message.getContent());
                })).block();
        }
    }

    private Consumer<EmbedCreateSpec> format(User user)
    {
        LocalDateTime ldt = LocalDateTime.now();
        return ChatUtil.user(user).andThen(e -> e.setFooter(ldt.getYear() + "/"
                    + String.format("%02d", ldt.getMonthValue()) + "/"
                    + String.format("%02d", ldt.getDayOfMonth()) + " "
                    + String.format("%02d", ldt.getHour()) + ":"
                    + String.format("%02d", ldt.getMinute()) + ":"
                    + String.format("%02d", ldt.getSecond()), null));
    }

    @Override
    public void register(GatewayDiscordClient gateway)
    {
        gateway.on(MessageUpdateEvent.class).subscribe(this::onMessageUpdate);
        gateway.on(MessageDeleteEvent.class).subscribe(this::onMessageDelete);
    }

    @Override
    public Snubot getParent()
    {
        return parent;
    }
}

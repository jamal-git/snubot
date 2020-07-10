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
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.TextChannel;
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
        if (!event.isContentChanged()) return;

        Message message = event.getMessage().block();

        TextChannel channel = event.getChannel().ofType(TextChannel.class).block();
        if (channel == null) return;

        Guild guild = message.getGuild().block();
        if (guild == null || !parent.hasGuildData(guild)) return;

        GuildData data = parent.getGuildData(guild);
        if (!data.hasLogChannel()) return;

        TextChannel logChannel = data.getLogChannel().block();
        User author = message.getAuthor().orElse(null);

        if (author != null)
            logChannel.createEmbed(ChatUtil.authorUser(author).andThen(base()).andThen(edit(channel, event.getOld().get(), message))).block();
        else
            logChannel.createEmbed(ChatUtil.authorGuild(guild).andThen(base()).andThen(edit(channel, event.getOld().get(), message))).block();
    }

    public void onMessageDelete(MessageDeleteEvent event)
    {
        Message message = event.getMessage().orElse(null);

        TextChannel channel = event.getChannel().ofType(TextChannel.class).block();
        if (channel == null) return;

        Guild guild = channel.getGuild().block();
        if (guild == null || !parent.hasGuildData(guild)) return;

        GuildData data = parent.getGuildData(guild);
        if (!data.hasLogChannel()) return;

        TextChannel logChannel = data.getLogChannel().block();

        if (message != null)
        {
            User author = message.getAuthor().orElse(null);

            if (author != null)
                logChannel.createEmbed(ChatUtil.authorUser(author).andThen(base()).andThen(delete(channel, message.getContent()))).block();
            else
                logChannel.createEmbed(ChatUtil.authorGuild(guild).andThen(base()).andThen(delete(channel, message.getContent()))).block();
        }
        else
            logChannel.createEmbed(ChatUtil.authorGuild(guild).andThen(base()).andThen(delete(channel, null))).block();
    }

    private Consumer<EmbedCreateSpec> base()
    {
        LocalDateTime ldt = LocalDateTime.now();
        return e -> e.setFooter(ldt.getYear() + "/"
                    + String.format("%02d", ldt.getMonthValue()) + "/"
                    + String.format("%02d", ldt.getDayOfMonth()) + " "
                    + String.format("%02d", ldt.getHour()) + ":"
                    + String.format("%02d", ldt.getMinute()) + ":"
                    + String.format("%02d", ldt.getSecond()), null);
    }

    private Consumer<EmbedCreateSpec> edit(Channel channel, Message old, Message now)
    {
        return e ->
        {
            e.setColor(Color.CYAN);
            e.setDescription("**Message edited in " + channel.getMention() + "** ([Jump to Message](" + ChatUtil.url(now) + "))");
            e.addField("Before", old.getContent().isEmpty() ? "None" : old.getContent(), false);
            e.addField("After", now.getContent().isEmpty() ? "None" : now.getContent(), false);
        };
    }

    private Consumer<EmbedCreateSpec> delete(Channel channel, String content)
    {
        return e ->
        {
            e.setColor(Color.RED);
            e.setDescription("**Message deleted in " + channel.getMention() + "**\n" + (content != null ? content : ""));
        };
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

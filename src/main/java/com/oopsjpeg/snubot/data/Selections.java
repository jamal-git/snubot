package com.oopsjpeg.snubot.data;

import com.oopsjpeg.snubot.Snubot;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import reactor.core.publisher.Mono;

public class Selections
{
    private String guildId;
    private String channelId;
    private String messageId;

    public Mono<Guild> getGuild()
    {
        return Snubot.getInstance().getGateway().getGuildById(Snowflake.of(guildId));
    }

    public String getGuildId()
    {
        return guildId;
    }

    public void setGuildId(String guildId)
    {
        this.guildId = guildId;
        setChannelId(null);
    }

    public Mono<TextChannel> getChannel()
    {
        return Snubot.getInstance().getGateway().getChannelById(getChannelId()).cast(TextChannel.class);
    }

    public String getChannelLink() {
        return "https://discord.com/channels/" + guildId + "/" + channelId;
    }

    public Snowflake getChannelId()
    {
        return Snowflake.of(channelId);
    }

    public void setChannelId(String channelId)
    {
        this.channelId = channelId;
        setMessageId(null);
    }

    public Mono<Message> getMessage()
    {
        return Snubot.getInstance().getGateway().getMessageById(getChannelId(), getMessageId());
    }

    public boolean hasMessage() {
        return messageId != null;
    }

    public String getMessageLink() {
        return getChannelLink() + "/" + messageId;
    }

    public Snowflake getMessageId()
    {
        return Snowflake.of(messageId);
    }

    public void setMessageId(String messageId)
    {
        this.messageId = messageId;
    }
}

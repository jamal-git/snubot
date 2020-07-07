package com.oopsjpeg.snubot.data.impl;

import com.oopsjpeg.snubot.data.ChildData;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import reactor.core.publisher.Mono;

public class Selections implements ChildData<UserData>
{
    private transient UserData parent;

    private String guildId;
    private String channelId;
    private String messageId;

    public String getGuildId()
    {
        return guildId;
    }

    public Snowflake getGuildIdAsSnowflake()
    {
        return Snowflake.of(guildId);
    }

    public Mono<Guild> getGuild()
    {
        return parent.getParent().getGateway().getGuildById(getGuildIdAsSnowflake());
    }

    public void setGuildId(String guildId)
    {
        this.guildId = guildId;
    }

    public void setGuildId(Snowflake guildId)
    {
        setGuildId(guildId.asString());
    }

    public void setGuild(Guild guild)
    {
        setGuildId(guild.getId());
    }

    public boolean hasGuild()
    {
        return guildId != null;
    }

    public String getChannelId()
    {
        return channelId;
    }

    public Snowflake getChannelIdAsSnowflake()
    {
        return Snowflake.of(channelId);
    }

    public Mono<TextChannel> getChannel()
    {
        return parent.getParent().getGateway().getChannelById(getChannelIdAsSnowflake()).cast(TextChannel.class);
    }

    public void setChannelId(String channelId)
    {
        this.channelId = channelId;
    }

    public void setChannelId(Snowflake channelId)
    {
        setChannelId(channelId.asString());
    }

    public void setChannel(TextChannel channel)
    {
        setGuildId(channel.getGuildId());
        setChannelId(channel.getId());
    }

    public boolean hasChannel()
    {
        return channelId != null;
    }

    public String getMessageId()
    {
        return messageId;
    }

    public Snowflake getMessageIdAsSnowflake()
    {
        return Snowflake.of(messageId);
    }

    public Mono<Message> getMessage()
    {
        return parent.getParent().getGateway().getMessageById(getChannelIdAsSnowflake(), getMessageIdAsSnowflake());
    }

    public void setMessageId(String messageId)
    {
        this.messageId = messageId;
    }

    public void setMessageId(Snowflake messageId)
    {
        setMessageId(messageId.asString());
    }

    public void setMessage(Message message)
    {
        setGuildId(message.getGuild().block().getId());
        setChannelId(message.getChannelId());
        setMessageId(message.getId());
    }

    public boolean hasMessage()
    {
        return messageId != null;
    }

    public String getGuildUrl()
    {
        return "https://discord.com/channels/" + guildId;
    }

    public String getChannelUrl()
    {
        return getGuildUrl() + "/" + channelId;
    }

    public String getMessageUrl()
    {
        return getChannelUrl() + "/" + messageId;
    }

    @Override
    public UserData getParent()
    {
        return parent;
    }

    @Override
    public void setParent(UserData parent)
    {
        this.parent = parent;
    }
}
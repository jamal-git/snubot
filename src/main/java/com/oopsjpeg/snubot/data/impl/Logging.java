package com.oopsjpeg.snubot.data.impl;

import com.oopsjpeg.snubot.data.ChildData;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.channel.TextChannel;
import reactor.core.publisher.Mono;

public class Logging implements ChildData<GuildData>
{
    private transient GuildData parent;

    private String channelId;

    public String getChannelId()
    {
        return channelId;
    }

    public Snowflake getChannelIdAsSnowflake()
    {
        return Snowflake.of(getChannelId());
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
        setChannelId(channel.getId());
    }

    public boolean hasChannel()
    {
        return getChannelId() != null;
    }

    @Override
    public GuildData getParent()
    {
        return parent;
    }

    @Override
    public void setParent(GuildData parent)
    {
        this.parent = parent;
    }
}

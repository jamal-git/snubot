package com.oopsjpeg.snubot.data.impl;

import com.oopsjpeg.snubot.data.ChildData;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.channel.TextChannel;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

public class Logging implements ChildData<GuildData>
{
    private final Set<String> ignoredChannelIds = new HashSet<>();

    private transient GuildData parent;

    private String channelId;

    public Set<String> getIgnoredChannelIds()
    {
        return ignoredChannelIds;
    }

    public void addIgnoredChannelId(String channelId)
    {
        ignoredChannelIds.add(channelId);
    }

    public void addIgnoredChannelId(Snowflake channelId)
    {
        addIgnoredChannelId(channelId.asString());
    }

    public void addIgnoredChannel(TextChannel channel)
    {
        addIgnoredChannelId(channel.getId());
    }

    public void removeIgnoredChannelId(String channelId)
    {
        ignoredChannelIds.remove(channelId);
    }

    public void removeIgnoredChannelId(Snowflake channelId)
    {
        removeIgnoredChannelId(channelId.asString());
    }

    public void removeIgnoredChannel(TextChannel channel)
    {
        removeIgnoredChannelId(channel.getId());
    }

    public boolean hasIgnoredChannelId(String channelId)
    {
        return ignoredChannelIds.contains(channelId);
    }

    public boolean hasIgnoredChannelId(Snowflake channelId)
    {
        return hasIgnoredChannelId(channelId.asString());
    }

    public boolean hasIgnoredChannel(TextChannel channel)
    {
        return hasIgnoredChannelId(channel.getId());
    }

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

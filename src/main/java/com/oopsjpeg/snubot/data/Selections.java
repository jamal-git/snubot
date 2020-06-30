package com.oopsjpeg.snubot.data;

import com.oopsjpeg.snubot.Snubot;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;
import reactor.core.publisher.Mono;

public class Selections
{
    private String guildId;
    private String channelId;
    private String messageId;

    public Selections() {}

    public Selections(@BsonProperty("guild_id") final String guildId, @BsonProperty("channel_id") final String channelId, @BsonProperty("message_id") final String messageId)
    {
        this.guildId = guildId;
        this.channelId = channelId;
        this.messageId = messageId;
    }

    @BsonProperty("guild_id")
    public String getGuildId()
    {
        return guildId;
    }

    @BsonProperty("guild_id")
    public void setGuildId(String guildId)
    {
        this.guildId = guildId;
        setChannelId(null);
    }

    @BsonIgnore
    public Mono<Guild> getGuild()
    {
        return Snubot.getInstance().getGateway().getGuildById(Snowflake.of(guildId));
    }

    @BsonIgnore
    public String getGuildLink()
    {
        return "https://discord.com/channels/" + guildId;
    }

    @BsonProperty("channel_id")
    public String getChannelId()
    {
        return channelId;
    }

    @BsonProperty("channel_id")
    public void setChannelId(String channelId)
    {
        this.channelId = channelId;
        setMessageId(null);
    }

    @BsonIgnore
    public Mono<TextChannel> getChannel()
    {
        return Snubot.getInstance().getGateway().getChannelById(Snowflake.of(channelId)).cast(TextChannel.class);
    }

    @BsonIgnore
    public String getChannelLink()
    {
        return getGuildLink() + "/" + channelId;
    }

    @BsonProperty("message_id")
    public String getMessageId()
    {
        return messageId;
    }

    @BsonProperty("message_id")
    public void setMessageId(String messageId)
    {
        this.messageId = messageId;
    }

    @BsonIgnore
    public Mono<Message> getMessage()
    {
        return Snubot.getInstance().getGateway().getMessageById(Snowflake.of(channelId), Snowflake.of(messageId));
    }

    public boolean hasMessage()
    {
        return messageId != null;
    }

    @BsonIgnore
    public String getMessageLink()
    {
        return getChannelLink() + "/" + messageId;
    }
}

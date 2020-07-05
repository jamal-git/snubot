package com.oopsjpeg.snubot.data;

import com.google.gson.annotations.SerializedName;
import com.oopsjpeg.snubot.Snubot;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import reactor.core.publisher.Mono;

public class Selections extends DataExtension<UserData>
{
    @SerializedName("guild_id")
    private String guildId;
    @SerializedName("channel_id")
    private String channelId;
    @SerializedName("message_id")
    private String messageId;

    public Mono<Guild> getGuild()
    {
        return Snubot.getInstance().getGateway().getGuildById(getGuildId());
    }

    public Mono<TextChannel> getChannel()
    {
        return Snubot.getInstance().getGateway().getChannelById(getChannelId()).cast(TextChannel.class);
    }

    public Mono<Message> getMessage()
    {
        return Snubot.getInstance().getGateway().getMessageById(getChannelId(), getMessageId());
    }

    public void setMessage(Message message)
    {
        guildId = message.getGuild().block().getId().asString();
        channelId = message.getChannelId().asString();
        messageId = message.getId().asString();
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

    public Snowflake getGuildId()
    {
        return Snowflake.of(guildId);
    }

    public Snowflake getChannelId()
    {
        return Snowflake.of(channelId);
    }

    public Snowflake getMessageId()
    {
        return Snowflake.of(messageId);
    }
}
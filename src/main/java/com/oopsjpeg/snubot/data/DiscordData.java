package com.oopsjpeg.snubot.data;

import com.google.gson.annotations.SerializedName;
import discord4j.common.util.Snowflake;

public abstract class DiscordData
{
    @SerializedName("_id")
    private final String id;

    public DiscordData(final String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    public Snowflake getIdAsSnowflake()
    {
        return Snowflake.of(id);
    }
}

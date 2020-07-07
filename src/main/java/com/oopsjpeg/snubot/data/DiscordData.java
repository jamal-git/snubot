package com.oopsjpeg.snubot.data;

import com.google.gson.annotations.SerializedName;
import discord4j.common.util.Snowflake;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o)
    {
        return o instanceof DiscordData && Objects.equals(id, ((DiscordData) o).id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }
}

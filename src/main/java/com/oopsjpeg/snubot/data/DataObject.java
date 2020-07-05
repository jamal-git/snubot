package com.oopsjpeg.snubot.data;

import discord4j.common.util.Snowflake;

public abstract class DataObject
{
    private final String id;

    private transient boolean markedForSave;

    public DataObject(final String id)
    {
        this.id = id;
    }

    public Snowflake getId()
    {
        return Snowflake.of(id);
    }

    public String getRawId()
    {
        return id;
    }

    public void markForSave()
    {
        markedForSave = true;
    }

    public boolean isMarkedForSave()
    {
        return markedForSave;
    }
}

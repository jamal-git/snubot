package com.oopsjpeg.snubot.data;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.Exclude;
import discord4j.common.util.Snowflake;

public class UserData
{
    private String id;
    private Selections selections = new Selections();

    public UserData() {}

    public UserData(String id)
    {
        this.id = id;
    }

    @DocumentId
    public String getId()
    {
        return id;
    }

    @Exclude
    public Snowflake getSnowflake()
    {
        return Snowflake.of(id);
    }

    @Exclude
    public Selections getSelections()
    {
        return selections;
    }
}
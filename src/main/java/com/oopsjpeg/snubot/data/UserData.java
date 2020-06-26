package com.oopsjpeg.snubot.data;

import discord4j.common.util.Snowflake;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;

public class UserData
{
    private String id;
    private Selections selections = new Selections();

    public UserData() {}

    public UserData(String id)
    {
        this.id = id;
    }

    @BsonId
    public String getId()
    {
        return id;
    }

    @BsonIgnore
    public Snowflake getSnowflake()
    {
        return Snowflake.of(id);
    }

    @BsonIgnore
    public Selections getSelections()
    {
        return selections;
    }

    @BsonIgnore
    public void setSelections(Selections selections)
    {
        this.selections = selections;
    }
}
package com.oopsjpeg.snubot.data;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class UserData
{
    private final String id;
    private final Selections selections;

    public UserData(String id)
    {
        this(id, new Selections());
    }

    @BsonCreator
    public UserData(@BsonProperty("id") String id, @BsonProperty("selections") Selections selections) {
        this.id = id;
        this.selections = selections;
    }

    @BsonId
    public String getId()
    {
        return id;
    }

    @BsonProperty("selections")
    public Selections getSelections()
    {
        return selections;
    }
}
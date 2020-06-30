package com.oopsjpeg.snubot.react;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class ReactRole
{
    private final String id;
    private final Type type;

    @BsonCreator
    public ReactRole(@BsonProperty("id") final String id, final @BsonProperty("type") Type type)
    {
        this.id = id;
        this.type = type;
    }

    @BsonId
    public String getId()
    {
        return id;
    }

    @BsonProperty("type")
    public Type getType()
    {
        return type;
    }

    public enum Type
    {
        TOGGLE("Toggle"),
        ONCE("Once");

        private final String name;

        Type(String name)
        {
            this.name = name;
        }

        public static Type fromName(String name)
        {
            for (Type t : values())
                if (t.name.equalsIgnoreCase(name)) return t;
            return null;
        }

        public String getName()
        {
            return name;
        }
    }
}

package com.oopsjpeg.snubot.react;

import discord4j.common.util.Snowflake;
import org.bson.codecs.pojo.annotations.BsonIgnore;

import java.util.Objects;

public class ReactRole
{
    private String id;
    private Type type;

    public ReactRole() {}

    public ReactRole(String id, Type type)
    {
        this.id = id;
        this.type = type;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public Type getType()
    {
        return type;
    }

    public void setType(Type type)
    {
        this.type = type;
    }

    @BsonIgnore
    public Snowflake getSnowflake()
    {
        return Snowflake.of(id);
    }

    @Override
    public boolean equals(Object o)
    {
        return o instanceof ReactRole && ((ReactRole) o).id.equals(id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
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

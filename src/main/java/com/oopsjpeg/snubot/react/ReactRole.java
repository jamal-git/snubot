package com.oopsjpeg.snubot.react;

import discord4j.common.util.Snowflake;

public class ReactRole
{
    private final String id;
    private final Type type;

    public ReactRole(final String id, final Type type)
    {
        this.id = id;
        this.type = type;
    }

    public Snowflake getId()
    {
        return Snowflake.of(id);
    }

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

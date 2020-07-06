package com.oopsjpeg.snubot.react;

import com.oopsjpeg.snubot.data.ChildData;
import com.oopsjpeg.snubot.data.DiscordData;

public class ReactRole extends DiscordData implements ChildData<ReactEmoji>
{
    private final Type type;

    private transient ReactEmoji parent;

    public ReactRole(final String id, final Type type)
    {
        super(id);
        this.type = type;
    }

    public Type getType()
    {
        return type;
    }

    @Override
    public ReactEmoji getParent()
    {
        return parent;
    }

    @Override
    public void setParent(ReactEmoji parent)
    {
        this.parent = parent;
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

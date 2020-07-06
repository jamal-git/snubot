package com.oopsjpeg.snubot.data.impl;

import com.oopsjpeg.snubot.Snubot;
import com.oopsjpeg.snubot.data.DiscordData;
import com.oopsjpeg.snubot.data.ChildData;
import com.oopsjpeg.snubot.data.SaveData;

public class UserData extends DiscordData implements ChildData<Snubot>, SaveData
{
    private final Selections selections = new Selections();

    private transient Snubot parent;
    private transient boolean markedForSave;

    public UserData(final String id)
    {
        super(id);
    }

    public Selections getSelections()
    {
        return (Selections) selections.parent(this);
    }

    @Override
    public Snubot getParent()
    {
        return parent;
    }

    @Override
    public void setParent(Snubot parent)
    {
        this.parent = parent;
    }

    @Override
    public boolean isMarkedForSave()
    {
        return markedForSave;
    }

    @Override
    public void setMarkedForSave(boolean markedForSave)
    {
        this.markedForSave = markedForSave;
    }
}
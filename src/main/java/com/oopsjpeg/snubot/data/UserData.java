package com.oopsjpeg.snubot.data;

public class UserData extends DataObject
{
    private final Selections selections = new Selections();

    public UserData(final String id)
    {
        super(id);
    }

    public Selections getSelections()
    {
        return (Selections) selections.parent(this);
    }
}
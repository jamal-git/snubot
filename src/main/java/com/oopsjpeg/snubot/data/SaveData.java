package com.oopsjpeg.snubot.data;

public interface SaveData
{
    default void markForSave()
    {
        setMarkedForSave(true);
    }

    boolean isMarkedForSave();

    void setMarkedForSave(boolean markedForSave);
}

package com.oopsjpeg.snubot.data;

public interface SaveData
{
    default void markForSave()
    {
        setMarkedForSave(true);
    }

    void setMarkedForSave(boolean markedForSave);

    boolean isMarkedForSave();
}

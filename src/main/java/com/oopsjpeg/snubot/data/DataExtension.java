package com.oopsjpeg.snubot.data;

public abstract class DataExtension<T>
{
    private transient T parent;

    public DataExtension<T> parent(T parent)
    {
        this.parent = parent;
        return this;
    }

    public T getParent() {
        return parent;
    }
}

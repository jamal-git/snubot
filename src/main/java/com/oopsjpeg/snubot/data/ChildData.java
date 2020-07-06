package com.oopsjpeg.snubot.data;

public interface ChildData<T>
{
    default ChildData<T> parent(T parent)
    {
        setParent(parent);
        return this;
    }

    T getParent();

    void setParent(T parent);
}

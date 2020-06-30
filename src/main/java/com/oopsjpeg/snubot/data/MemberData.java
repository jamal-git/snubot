package com.oopsjpeg.snubot.data;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Random;

public class MemberData
{
    private static final Random RANDOM = new Random();

    private final String id;

    private int xp;
    private int level;

    private long lastMessageMillis;

    public MemberData(final String id)
    {
        this.id = id;
    }

    @BsonCreator
    public MemberData(@BsonProperty("id") final String id, @BsonProperty("xp") final int xp, @BsonProperty("level") final int level)
    {
        this.id = id;
        this.xp = xp;
        this.level = level;
    }

    public static int xpRequired(int level)
    {
        return (int) (220 + Math.pow(level * 90, 1.06));
    }

    @BsonId
    public String getId()
    {
        return id;
    }

    @BsonProperty("xp")
    public int getXp()
    {
        return xp;
    }

    @BsonProperty("xp")
    public boolean setXp(int xp)
    {
        this.xp = xp;

        int newLevel = level;
        while (newLevel < 50 && this.xp >= xpRequired())
        {
            this.xp -= xpRequired();
            newLevel++;
        }

        if (level != newLevel) {
            level = newLevel;
            return true;
        }

        return false;
    }

    @BsonIgnore
    public boolean addXp(int xp)
    {
        return setXp(getXp() + xp);
    }

    @BsonIgnore
    public boolean messageXp()
    {
        if (System.currentTimeMillis() - lastMessageMillis > 60000)
        {
            lastMessageMillis = System.currentTimeMillis();
            return addXp(16 + RANDOM.nextInt(10));
        }
        return false;
    }

    @BsonIgnore
    public int xpRequired()
    {
        return xpRequired(level);
    }

    @BsonProperty("level")
    public int getLevel()
    {
        return level;
    }

    @BsonProperty("level")
    public void setLevel(int level)
    {
        this.level = level;
        xp = 0;
    }

    @BsonIgnore
    public long getLastMessageMillis()
    {
        return lastMessageMillis;
    }

    @BsonIgnore
    public void setLastMessageMillis(long lastMessageMillis)
    {
        this.lastMessageMillis = lastMessageMillis;
    }
}

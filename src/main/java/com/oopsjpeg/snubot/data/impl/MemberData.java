package com.oopsjpeg.snubot.data.impl;

import com.oopsjpeg.snubot.data.ChildData;
import com.oopsjpeg.snubot.data.DiscordData;

import java.util.Random;

public class MemberData extends DiscordData implements ChildData<GuildData>
{
    private static final Random RANDOM = new Random();

    private transient GuildData parent;
    private transient long lastMessageMillis;

    private int xp;
    private int level;

    public MemberData(final String id)
    {
        super(id);
    }

    public static int maxXp(int level)
    {
        return (int) (220 + Math.pow(level * 90, 1.06));
    }

    public int getXp()
    {
        return xp;
    }

    public void setXp(int xp)
    {
        this.xp = xp;
    }

    public void addXp(int xp)
    {
        setXp(getXp() + xp);
    }

    public boolean messageXp()
    {
        if (System.currentTimeMillis() - lastMessageMillis > 60000)
        {
            lastMessageMillis = System.currentTimeMillis();
            addXp(24 + RANDOM.nextInt(10));
            return true;
        }
        return false;
    }

    public int getMaxXp()
    {
        return maxXp(level);
    }

    public int getLevel()
    {
        return level;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    public boolean levelUp()
    {
        int newLevel = level;
        while (newLevel < 50 && this.xp >= maxXp(newLevel))
        {
            this.xp -= maxXp(newLevel);
            newLevel++;
        }

        if (level != newLevel)
        {
            level = newLevel;
            return true;
        }

        return false;
    }

    @Override
    public GuildData getParent()
    {
        return parent;
    }

    @Override
    public void setParent(GuildData parent)
    {
        this.parent = parent;
    }
}
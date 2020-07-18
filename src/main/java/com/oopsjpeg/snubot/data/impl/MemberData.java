package com.oopsjpeg.snubot.data.impl;

import com.oopsjpeg.snubot.data.ChildData;
import com.oopsjpeg.snubot.data.DiscordData;
import discord4j.core.object.entity.Member;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Random;

public class MemberData extends DiscordData implements ChildData<GuildData>
{
    public static final float DAILY_COINS = 250;
    public static final float DAILY_XP = 0.1f;

    private static final Random RANDOM = new Random();

    private transient GuildData parent;
    private transient LocalDateTime lastMessageTime;

    private float coins;

    private float xp;
    private int level;

    private LocalDateTime lastDailyTime;

    public MemberData(final String id)
    {
        super(id);
    }

    public static int maxXp(int level)
    {
        return (int) (220 + Math.pow(level * 90, 1.06));
    }

    public Mono<Member> discord()
    {
        return parent.getParent().getGateway().getMemberById(parent.getIdAsSnowflake(), getIdAsSnowflake());
    }

    public boolean message()
    {
        if (lastMessageTime == null || LocalDateTime.now().isAfter(lastMessageTime.plusMinutes(1)))
        {
            lastMessageTime = LocalDateTime.now();
            addCoins(3 + RANDOM.nextInt(4));
            addXp(24 + RANDOM.nextInt(11));
            return true;
        }
        return false;
    }

    public float getCoins()
    {
        return coins;
    }

    public void setCoins(float coins)
    {
        this.coins = coins;
    }

    public void addCoins(float coins)
    {
        setCoins(getCoins() + coins);
    }

    public void subCoins(float coins)
    {
        setCoins(getCoins() - coins);
    }

    public float getXp()
    {
        return xp;
    }

    public void setXp(float xp)
    {
        this.xp = xp;
    }

    public void addXp(float xp)
    {
        setXp(getXp() + xp);
        levelUp();
    }

    public float getMaxXp()
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

    public void levelUp()
    {
        int newLevel = level;
        while (newLevel < parent.getLeveling().getMaxLevel() && this.xp >= maxXp(newLevel))
        {
            this.xp -= maxXp(newLevel);
            newLevel++;
        }

        if (level != newLevel)
        {
            level = newLevel;
            parent.getLeveling().syncRoles(getId());
        }
    }

    public void daily()
    {
        lastDailyTime = LocalDateTime.now();
        addCoins(getDailyCoins());
        addXp(getDailyXp());
    }

    public float getDailyCoins()
    {
        return DAILY_COINS;
    }

    public float getDailyXp()
    {
        return getMaxXp() * DAILY_XP;
    }

    public boolean hasDaily()
    {
        return lastDailyTime == null || LocalDateTime.now().isAfter(lastDailyTime.plusDays(1));
    }

    public LocalDateTime getLastDailyTime()
    {
        return lastDailyTime;
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
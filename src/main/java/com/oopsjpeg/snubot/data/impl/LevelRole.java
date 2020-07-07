package com.oopsjpeg.snubot.data.impl;

import com.oopsjpeg.snubot.data.ChildData;
import com.oopsjpeg.snubot.data.DiscordData;
import discord4j.core.object.entity.Role;
import reactor.core.publisher.Mono;

public class LevelRole extends DiscordData implements ChildData<Leveling>
{
    private final int level;

    private transient Leveling parent;

    public LevelRole(String id, int level)
    {
        super(id);
        this.level = level;
    }

    public Mono<Role> getRole()
    {
        return parent.getParent().getParent().getGateway().getRoleById(parent.getParent().getIdAsSnowflake(), getIdAsSnowflake());
    }

    public int getLevel()
    {
        return level;
    }

    @Override
    public Leveling getParent()
    {
        return parent;
    }

    @Override
    public void setParent(Leveling parent)
    {
        this.parent = parent;
    }
}

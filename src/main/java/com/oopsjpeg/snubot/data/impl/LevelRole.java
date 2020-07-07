package com.oopsjpeg.snubot.data.impl;

import com.oopsjpeg.snubot.data.ChildData;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Role;
import reactor.core.publisher.Mono;

import java.util.Objects;

public class LevelRole implements ChildData<Leveling>
{
    private final String roleId;
    private final int level;

    private transient Leveling parent;

    public LevelRole(String roleId, int level)
    {
        this.roleId = roleId;
        this.level = level;
    }

    public String getRoleId()
    {
        return roleId;
    }

    public Snowflake getRoleIdAsSnowflake()
    {
        return Snowflake.of(roleId);
    }

    public Mono<Role> getRole()
    {
        return parent.getParent().getParent().getGateway().getRoleById(parent.getParent().getIdAsSnowflake(), getRoleIdAsSnowflake());
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

    @Override
    public boolean equals(Object o)
    {
        return o instanceof LevelRole
                && level == ((LevelRole) o).level
                && roleId.equals(((LevelRole) o).roleId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(roleId, level);
    }
}

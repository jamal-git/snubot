package com.oopsjpeg.snubot.data.impl;

import com.oopsjpeg.snubot.data.ChildData;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.User;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Coloring implements ChildData<GuildData>
{
    private final Set<String> roleIdSet = new HashSet<>();

    private transient GuildData parent;

    private int levelRequired;

    public Set<String> getRoleIdSet()
    {
        return roleIdSet;
    }

    public Set<Role> getRoleSet()
    {
        return roleIdSet.stream().map(id -> parent.getParent().getGateway().getRoleById(parent.getIdAsSnowflake(), Snowflake.of(id)).block()).collect(Collectors.toSet());
    }

    public void addRole(String id)
    {
        roleIdSet.add(id);
    }

    public void addRole(Snowflake id)
    {
        addRole(id.asString());
    }

    public void addRole(Role role)
    {
        addRole(role.getId());
    }

    public void removeRole(String id)
    {
        roleIdSet.remove(id);
    }

    public void removeRole(Snowflake id)
    {
        removeRole(id.asString());
    }

    public void removeRole(Role role)
    {
        removeRole(role.getId());
    }

    public boolean hasRole(String id)
    {
        return roleIdSet.contains(id);
    }

    public boolean hasRole(Snowflake id)
    {
        return hasRole(id.asString());
    }

    public boolean hasRole(Role role)
    {
        return hasRole(role.getId());
    }

    public void clearRoles()
    {
        roleIdSet.clear();
    }

    public void setUserColor(User user, Role role)
    {
        Member member = user.asMember(parent.getIdAsSnowflake()).block();

        if (!member.getRoleIds().contains(role.getId()))
            member.addRole(role.getId()).block();

        roleIdSet.stream().map(Snowflake::of)
                .filter(id -> !role.getId().equals(id))
                .filter(id -> member.getRoleIds().contains(id))
                .forEach(id -> member.removeRole(id).block());
    }

    public int getLevelRequired()
    {
        return levelRequired;
    }

    public void setLevelRequired(int levelRequired)
    {
        this.levelRequired = levelRequired;
    }

    public boolean hasLevelRequired()
    {
        return levelRequired != 0;
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

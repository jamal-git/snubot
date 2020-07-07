package com.oopsjpeg.snubot.data.impl;

import com.oopsjpeg.snubot.data.ChildData;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Leveling implements ChildData<GuildData>
{
    private final Map<String, LevelRole> roleMap = new HashMap<>();

    private transient GuildData parent;

    private int maxLevel;

    public Map<String, LevelRole> getRoleMap()
    {
        return roleMap;
    }

    public List<Role> getRolesForLevel(int level)
    {
        return roleMap.values().stream()
                .filter(r -> r.getLevel() == level)
                .map(r -> r.getRole().block())
                .collect(Collectors.toList());
    }

    public LevelRole getRole(String id)
    {
        return (LevelRole) roleMap.get(id).parent(this);
    }

    public LevelRole getRole(Snowflake id)
    {
        return getRole(id.asString());
    }

    public LevelRole getRole(Role role)
    {
        return getRole(role.getId());
    }

    public LevelRole addRole(String id, int level)
    {
        roleMap.put(id, new LevelRole(id, level));
        return getRole(id);
    }

    public LevelRole addRole(Snowflake id, int level)
    {
        return addRole(id.asString(), level);
    }

    public LevelRole addRole(Role role, int level)
    {
        return addRole(role.getId(), level);
    }

    public void removeRole(String id)
    {
        roleMap.remove(id);
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
        return roleMap.containsKey(id);
    }

    public boolean hasRole(Snowflake id)
    {
        return hasRole(id.asString());
    }

    public boolean hasRole(Role role)
    {
        return hasRole(role.getId());
    }

    public void syncRoles(User user)
    {
        if (getParent().hasMemberData(user))
        {
            Member member = user.asMember(getParent().getIdAsSnowflake()).block();
            MemberData memberData = getParent().getMemberData(user);

            roleMap.values().forEach(r ->
            {
                if (memberData.getLevel() >= r.getLevel() && !member.getRoleIds().contains(r.getRoleIdAsSnowflake()))
                    member.addRole(r.getRoleIdAsSnowflake()).block();

                if (memberData.getLevel() < r.getLevel() && member.getRoleIds().contains(r.getRoleIdAsSnowflake()))
                    member.removeRole(r.getRoleIdAsSnowflake()).block();
            });
        }
    }

    public int getMaxLevel()
    {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel)
    {
        this.maxLevel = maxLevel;
    }

    public boolean hasMaxLevel()
    {
        return maxLevel != 0;
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

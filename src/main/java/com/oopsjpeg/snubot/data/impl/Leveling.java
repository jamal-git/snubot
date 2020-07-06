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
    private final Map<String, Integer> rolesMap = new HashMap<>();

    private transient GuildData parent;

    private int maxLevel;

    public Map<String, Integer> getRolesMap()
    {
        return rolesMap;
    }

    public List<Role> getRoles(int level)
    {
        return rolesMap.entrySet().stream()
                .filter(e -> level == e.getValue())
                .map(e -> parent.getParent().getGateway().getRoleById(getParent().getIdAsSnowflake(), Snowflake.of(e.getKey())).block())
                .collect(Collectors.toList());
    }

    public void addRole(Role role, int level)
    {
        rolesMap.put(role.getId().asString(), level);
    }

    public void removeRole(Role role)
    {
        rolesMap.remove(role.getId().asString());
    }

    public boolean hasRole(Role role)
    {
        return rolesMap.containsKey(role.getId().asString());
    }

    public void syncRoles(User user)
    {
        if (getParent().hasMemberData(user))
        {
            Member member = user.asMember(getParent().getIdAsSnowflake()).block();
            MemberData memberData = getParent().getMemberData(user);

            rolesMap.forEach((id, level) ->
            {
                level--;
                Snowflake roleId = Snowflake.of(id);

                if (memberData.getLevel() >= level && !member.getRoleIds().contains(roleId))
                    member.addRole(roleId).block();

                if (memberData.getLevel() < level && member.getRoleIds().contains(roleId))
                    member.removeRole(roleId).block();
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

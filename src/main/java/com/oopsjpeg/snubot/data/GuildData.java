package com.oopsjpeg.snubot.data;

import com.oopsjpeg.snubot.Snubot;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

public class GuildData extends DataObject
{
    private Map<String, MemberData> memberDataMap = new HashMap<>();

    private final Leveling leveling = new Leveling();

    private String modRoleId;

    public GuildData(final String id)
    {
        super(id);
    }

    public Mono<Guild> getGuild()
    {
        return Snubot.getInstance().getGateway().getGuildById(getId());
    }

    public Map<String, MemberData> getMemberDataMap()
    {
        return memberDataMap;
    }

    public MemberData getMemberData(String id)
    {
        return memberDataMap.getOrDefault(id, null).parent(this);
    }

    public MemberData getMemberData(User user)
    {
        return getMemberData(user.getId().asString());
    }

    public void addMemberData(String id)
    {
        getMemberDataMap().put(id, new MemberData(id));
    }

    public MemberData getOrAddMemberData(String id)
    {
        if (!getMemberDataMap().containsKey(id))
            addMemberData(id);
        return getMemberData(id);
    }

    public MemberData getOrAddMemberData(User user)
    {
        return getOrAddMemberData(user.getId().asString());
    }

    public boolean hasMemberData(User user)
    {
        return memberDataMap.containsKey(user.getId().asString());
    }

    public Leveling getLeveling()
    {
        return (Leveling) leveling.parent(this);
    }

    public Mono<Role> getModRole()
    {
        return Snubot.getInstance().getGateway().getRoleById(getId(), getModRoleId());
    }

    public void setModRole(Role role)
    {
        modRoleId = role.getId().asString();
    }

    public Snowflake getModRoleId()
    {
        return Snowflake.of(modRoleId);
    }

    public boolean hasModRole()
    {
        return modRoleId != null;
    }
}

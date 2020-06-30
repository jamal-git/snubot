package com.oopsjpeg.snubot.data;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.HashMap;
import java.util.Map;

public class GuildData
{
    private final String id;
    private final Map<String, MemberData> memberDataMap;
    private final Map<Integer, String> leveledRolesMap;

    private boolean levelingEnabled;

    public GuildData(final String id)
    {
        this(id, new HashMap<>(), new HashMap<>());
    }

    @BsonCreator
    public GuildData(@BsonProperty("id") final String id,
                     @BsonProperty("member_data_map") final Map<String, MemberData> memberDataMap,
                     @BsonProperty("leveled_roles_map") final Map<Integer, String> leveledRolesMap)
    {
        this.id = id;
        this.memberDataMap = memberDataMap;
        this.leveledRolesMap = leveledRolesMap;
    }

    @BsonId
    public String getId()
    {
        return id;
    }

    @BsonProperty("member_data_map")
    public Map<String, MemberData> getMemberDataMap()
    {
        return memberDataMap;
    }

    @BsonIgnore
    public MemberData getMemberData(String id)
    {
        return memberDataMap.getOrDefault(id, null);
    }

    @BsonIgnore
    public void addMemberData(String id)
    {
        memberDataMap.put(id, new MemberData(id));
    }

    public MemberData getOrAddMemberData(String id)
    {
        if (!memberDataMap.containsKey(id))
            addMemberData(id);
        return getMemberData(id);
    }

    public Map<Integer, String> getLeveledRolesMap()
    {
        return leveledRolesMap;
    }

    public void updateLeveledRoles(Member member) {
        if (memberDataMap.containsKey(member.getId().asString())) {
            MemberData memberData = memberDataMap.get(member.getId().asString());
            for (int i = 0; i < 50; i++) if (leveledRolesMap.containsKey(i)) {
                Snowflake roleId = Snowflake.of(leveledRolesMap.get(i));
                if (memberData.getLevel() >= i && !member.getRoleIds().contains(roleId))
                    member.addRole(roleId).block();
                if (memberData.getLevel() < i && member.getRoleIds().contains(roleId))
                    member.removeRole(roleId).block();
            }
        }
    }

    @BsonProperty("leveling_enabled")
    public boolean isLevelingEnabled()
    {
        return levelingEnabled;
    }

    @BsonProperty("leveling_enabled")
    public void setLevelingEnabled(boolean levelingEnabled)
    {
        this.levelingEnabled = levelingEnabled;
    }
}

package com.oopsjpeg.snubot.data.impl;

import com.oopsjpeg.snubot.Snubot;
import com.oopsjpeg.snubot.data.ChildData;
import com.oopsjpeg.snubot.data.DiscordData;
import com.oopsjpeg.snubot.data.SaveData;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

public class GuildData extends DiscordData implements ChildData<Snubot>, SaveData
{
    private final Map<String, MemberData> memberDataMap = new HashMap<>();
    private Leveling leveling = new Leveling();
    private Coloring coloring = new Coloring();
    private Logging logging = new Logging();

    private transient Snubot parent;
    private transient boolean markedForSave;

    private String modRoleId;
    private String logChannelId;

    public GuildData(final String id)
    {
        super(id);
    }

    public Mono<Guild> discord()
    {
        return parent.getGateway().getGuildById(getIdAsSnowflake());
    }

    public Map<String, MemberData> getMemberDataMap()
    {
        return memberDataMap;
    }

    public MemberData getMemberData(String id)
    {
        return (MemberData) memberDataMap.get(id).parent(this);
    }

    public MemberData getMemberData(Snowflake id)
    {
        return getMemberData(id.asString());
    }

    public MemberData getMemberData(User user)
    {
        return getMemberData(user.getId());
    }

    public MemberData addMemberData(String id)
    {
        memberDataMap.put(id, new MemberData(id));
        return getMemberData(id);
    }

    public MemberData addMemberData(Snowflake id)
    {
        return addMemberData(id.asString());
    }

    public MemberData addMemberData(User user)
    {
        return addMemberData(user.getId());
    }

    public void removeMemberData(String id)
    {
        memberDataMap.remove(id);
    }

    public void removeMemberData(Snowflake id)
    {
        removeMemberData(id.asString());
    }

    public void removeMemberData(User user)
    {
        removeMemberData(user.getId());
    }

    public boolean hasMemberData(String id)
    {
        return memberDataMap.containsKey(id);
    }

    public boolean hasMemberData(Snowflake id)
    {
        return hasMemberData(id.asString());
    }

    public boolean hasMemberData(User user)
    {
        return hasMemberData(user.getId());
    }

    public MemberData getOrAddMemberData(String id)
    {
        if (!hasMemberData(id))
            return addMemberData(id);
        return getMemberData(id);
    }

    public MemberData getOrAddMemberData(Snowflake id)
    {
        return getOrAddMemberData(id.asString());
    }

    public MemberData getOrAddMemberData(User user)
    {
        return getOrAddMemberData(user.getId());
    }

    public Leveling getLeveling()
    {
        if (leveling == null)
            leveling = new Leveling();
        return (Leveling) leveling.parent(this);
    }

    public Coloring getColoring()
    {
        if (coloring == null)
            coloring = new Coloring();
        return (Coloring) coloring.parent(this);
    }

    public Logging getLogging()
    {
        if (logging == null)
            logging = new Logging();
        return (Logging) logging.parent(this);
    }

    public String getModRoleId()
    {
        return modRoleId;
    }

    public Snowflake getModRoleIdAsSnowflake()
    {
        return Snowflake.of(getModRoleId());
    }

    public Mono<Role> getModRole()
    {
        return parent.getGateway().getRoleById(getIdAsSnowflake(), getModRoleIdAsSnowflake());
    }

    public void setModRoleId(String modRoleId)
    {
        this.modRoleId = modRoleId;
    }

    public void setModRoleId(Snowflake modRoleId)
    {
        setModRoleId(modRoleId.asString());
    }

    public void setModRole(Role modRole)
    {
        setModRoleId(modRole.getId());
    }

    public boolean hasModRole()
    {
        return getModRoleId() != null;
    }

    @Override
    public Snubot getParent()
    {
        return parent;
    }

    @Override
    public void setParent(Snubot parent)
    {
        this.parent = parent;
    }

    @Override
    public boolean isMarkedForSave()
    {
        return markedForSave;
    }

    @Override
    public void setMarkedForSave(boolean markedForSave)
    {
        this.markedForSave = markedForSave;
    }
}

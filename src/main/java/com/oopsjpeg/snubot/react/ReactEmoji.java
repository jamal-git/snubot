package com.oopsjpeg.snubot.react;

import com.oopsjpeg.snubot.data.ChildData;
import com.oopsjpeg.snubot.util.Util;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Role;
import discord4j.core.object.reaction.ReactionEmoji;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ReactEmoji implements ChildData<ReactMessage>
{
    private final String text;
    private final Map<String, ReactRole> roleMap = new HashMap<>();

    private transient ReactMessage parent;

    public ReactEmoji(final String text)
    {
        this.text = text;
    }

    public String getText()
    {
        return text;
    }

    public ReactionEmoji getReaction()
    {
        return Util.stringToEmoji(text);
    }

    public Map<String, ReactRole> getRoleMap()
    {
        return roleMap;
    }

    public List<ReactRole> getRoleList()
    {
        return new LinkedList<>(roleMap.values());
    }

    public ReactRole getRole(String id)
    {
        return (ReactRole) roleMap.get(id).parent(this);
    }

    public ReactRole getRole(Snowflake id)
    {
        return getRole(id.asString());
    }

    public ReactRole getRole(Role role)
    {
        return getRole(role.getId());
    }

    public ReactRole addRole(String id, ReactRole.Type type)
    {
        roleMap.put(id, new ReactRole(id, type));
        return getRole(id);
    }

    public ReactRole addRole(Snowflake id, ReactRole.Type type)
    {
        return addRole(id.asString(), type);
    }

    public ReactRole addRole(Role role, ReactRole.Type type)
    {
        return addRole(role.getId(), type);
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

    @Override
    public ReactMessage getParent()
    {
        return parent;
    }

    @Override
    public void setParent(ReactMessage parent)
    {
        this.parent = parent;
    }
}

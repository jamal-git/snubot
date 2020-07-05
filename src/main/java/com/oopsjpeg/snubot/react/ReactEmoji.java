package com.oopsjpeg.snubot.react;

import com.oopsjpeg.snubot.util.Util;
import discord4j.core.object.entity.Role;
import discord4j.core.object.reaction.ReactionEmoji;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ReactEmoji
{
    private final String text;
    private final Map<String, ReactRole> roleMap = new HashMap<>();

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

    public void addRole(String id, ReactRole.Type type)
    {
        roleMap.put(id, new ReactRole(id, type));
    }

    public void addRole(Role role, ReactRole.Type type)
    {
        addRole(role.getId().asString(), type);
    }

    public void removeRole(Role role)
    {
        roleMap.remove(role.getId().asString());
    }
}

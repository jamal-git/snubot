package com.oopsjpeg.snubot.react;

import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ReactEmote
{
    private String emoji;
    private List<ReactRole> roleList = new ArrayList<>();

    public ReactEmote() {}

    public ReactEmote(String emoji)
    {
        this.emoji = emoji;
    }

    public String getEmoji()
    {
        return emoji;
    }

    public void setEmoji(String emoji)
    {
        this.emoji = emoji;
    }

    @BsonProperty("role_list")
    public List<ReactRole> getRoleList()
    {
        return roleList;
    }

    @BsonProperty("role_list")
    public void setRoleList(List<ReactRole> roleList)
    {
        this.roleList = roleList;
    }

    public void addRole(String id, ReactRole.Type type)
    {
        if (hasRole(id))
            removeRole(id);
        roleList.add(new ReactRole(id, type));
    }

    public void removeRole(String id)
    {
        roleList.removeIf(role -> role.getId().equals(id));
    }

    public boolean hasRole(String id) {
        return roleList.stream().anyMatch(role -> role.getId().equals(id));
    }
}

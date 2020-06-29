package com.oopsjpeg.snubot.react;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.HashMap;
import java.util.Map;

public class ReactReaction
{
    private final String emoji;
    private final Map<String, ReactRole> roleMap;

    public ReactReaction(String emoji)
    {
        this(emoji, new HashMap<>());
    }

    @BsonCreator
    public ReactReaction(@BsonProperty("emoji") String emoji, @BsonProperty("role_map") Map<String, ReactRole> roleMap) {
        this.emoji = emoji;
        this.roleMap = roleMap;
    }

    @BsonId
    public String getEmoji()
    {
        return emoji;
    }

    @BsonProperty("role_map")
    public Map<String, ReactRole> getRoleMap()
    {
        return roleMap;
    }

    @BsonIgnore
    public void addRole(String id, ReactRole.Type type)
    {
        roleMap.put(id, new ReactRole(id, type));
    }
}

package com.oopsjpeg.snubot.react;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.HashMap;
import java.util.Map;

public class ReactContainer
{
    private final String id;
    private final Map<String, ReactReaction> reactionMap;

    public ReactContainer(final String id)
    {
        this(id, new HashMap<>());
    }

    @BsonCreator
    public ReactContainer(@BsonProperty("id") final String id, @BsonProperty("reaction_map") final Map<String, ReactReaction> reactionMap)
    {
        this.id = id;
        this.reactionMap = reactionMap;
    }

    @BsonId
    public String getId()
    {
        return id;
    }

    @BsonProperty("reaction_map")
    public Map<String, ReactReaction> getReactionMap()
    {
        return reactionMap;
    }

    @BsonIgnore
    public int getReactionCount()
    {
        return reactionMap.size();
    }

    @BsonIgnore
    public long getRoleCount()
    {
        return reactionMap.values().stream().map(ReactReaction::getRoleMap).distinct().count();
    }

    @BsonIgnore
    public ReactReaction getReaction(String emoji)
    {
        return reactionMap.getOrDefault(emoji, null);
    }

    @BsonIgnore
    public void addReaction(String emoji)
    {
        reactionMap.put(emoji, new ReactReaction(emoji));
    }

    @BsonIgnore
    public ReactReaction getOrAddReaction(String emoji)
    {
        if (!reactionMap.containsKey(emoji))
            addReaction(emoji);
        return getReaction(emoji);
    }
}

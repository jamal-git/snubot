package com.oopsjpeg.snubot.react;

import discord4j.common.util.Snowflake;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ReactContainer
{
    private String id;
    private List<ReactEmote> emoteList = new ArrayList<>();

    public ReactContainer() {}

    public ReactContainer(String id)
    {
        this.id = id;
    }

    @BsonId
    public String getId()
    {
        return id;
    }

    @BsonId
    public void setId(String id)
    {
        this.id = id;
    }

    @BsonIgnore
    public Snowflake getSnowflake()
    {
        return Snowflake.of(id);
    }

    @BsonProperty("emote_list")
    public List<ReactEmote> getEmoteList()
    {
        return emoteList;
    }

    @BsonProperty("emote_list")
    public void setEmoteList(List<ReactEmote> emoteList)
    {
        this.emoteList = emoteList;
    }

    @BsonIgnore
    public long getRoleCount() {
        return emoteList.stream().map(ReactEmote::getRoleList).distinct().count();
    }

    @BsonIgnore
    public ReactEmote getReaction(String emoji)
    {
        return emoteList.stream().filter(emote -> emote.getEmoji().equals(emoji)).findAny().orElse(null);
    }

    @BsonIgnore
    public ReactEmote getOrAddReaction(String emoji) {
        if (!hasReaction(emoji))
            addReaction(emoji);
        return getReaction(emoji);
    }

    public void addReaction(String emoji) {
        if (hasReaction(emoji))
            removeReaction(emoji);
        emoteList.add(new ReactEmote(emoji));
    }

    public void removeReaction(String emoji)
    {
        emoteList.removeIf(emote -> emote.getEmoji().equals(emoji));
    }

    public boolean hasReaction(String emoji)
    {
        return emoteList.stream().anyMatch(emote -> emote.getEmoji().equals(emoji));
    }
}

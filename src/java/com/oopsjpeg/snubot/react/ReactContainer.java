package com.oopsjpeg.snubot.react;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.Exclude;
import com.google.cloud.firestore.annotation.PropertyName;
import discord4j.common.util.Snowflake;

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

    @DocumentId
    public String getId()
    {
        return id;
    }

    @Exclude
    public Snowflake getSnowflake()
    {
        return Snowflake.of(id);
    }

    @PropertyName("emote_list")
    public List<ReactEmote> getEmoteList()
    {
        return emoteList;
    }

    @PropertyName("emote_list")
    public void setEmoteList(List<ReactEmote> emoteList)
    {
        this.emoteList = emoteList;
    }

    @Exclude
    public long getRoleCount() {
        return emoteList.stream().map(ReactEmote::getRoleList).distinct().count();
    }

    @Exclude
    public ReactEmote getReaction(String emoji)
    {
        return emoteList.stream().filter(emote -> emote.getEmoji().equals(emoji)).findAny().orElse(null);
    }

    @Exclude
    public ReactEmote getOrAddReaction(String emoji) {
        if (!hasReaction(emoji))
            addReaction(emoji);
        return getReaction(emoji);
    }

    @Exclude
    public void addReaction(String emoji) {
        if (hasReaction(emoji))
            removeReaction(emoji);
        emoteList.add(new ReactEmote(emoji));
    }

    @Exclude
    public void removeReaction(String emoji)
    {
        emoteList.removeIf(emote -> emote.getEmoji().equals(emoji));
    }

    @Exclude
    public boolean hasReaction(String emoji)
    {
        return emoteList.stream().anyMatch(emote -> emote.getEmoji().equals(emoji));
    }
}

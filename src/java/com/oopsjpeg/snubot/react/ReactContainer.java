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
    private List<ReactReaction> reactionList = new ArrayList<>();

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

    @PropertyName("reaction_list")
    public List<ReactReaction> getReactionList()
    {
        return reactionList;
    }

    @PropertyName("reaction_list")
    public void setReactionList(List<ReactReaction> reactionList)
    {
        this.reactionList = reactionList;
    }

    @Exclude
    public long getRoleCount() {
        return reactionList.stream().map(ReactReaction::getRoleList).distinct().count();
    }

    @Exclude
    public ReactReaction getReaction(String emoji)
    {
        return reactionList.stream().filter(reaction -> reaction.getEmoji().equals(emoji)).findAny().orElse(null);
    }

    @Exclude
    public ReactReaction getOrAddReaction(String emoji) {
        if (!hasReaction(emoji))
            addReaction(emoji);
        return getReaction(emoji);
    }

    @Exclude
    public void addReaction(String emoji) {
        if (hasReaction(emoji))
            removeReaction(emoji);
        reactionList.add(new ReactReaction(emoji));
    }

    @Exclude
    public void removeReaction(String emoji)
    {
        reactionList.removeIf(reaction -> reaction.getEmoji().equals(emoji));
    }

    @Exclude
    public boolean hasReaction(String emoji)
    {
        return reactionList.stream().anyMatch(reaction -> reaction.getEmoji().equals(emoji));
    }
}

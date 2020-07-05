package com.oopsjpeg.snubot.react;

import com.oopsjpeg.snubot.Snubot;
import com.oopsjpeg.snubot.data.DataObject;
import com.oopsjpeg.snubot.util.Util;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ReactMessage extends DataObject
{
    private final String channelId;
    private final Map<String, ReactEmoji> emojiMap = new HashMap<>();

    public ReactMessage(final String id, final String channelId)
    {
        super(id);
        this.channelId = channelId;
    }

    public Mono<Message> getMessage()
    {
        return Snubot.getInstance().getGateway().getMessageById(getChannelId(), getId());
    }

    public Snowflake getChannelId()
    {
        return Snowflake.of(channelId);
    }

    public Map<String, ReactEmoji> getEmojiMap()
    {
        return emojiMap;
    }

    public int getEmojiCount()
    {
        return emojiMap.size();
    }

    public List<ReactEmoji> getEmojiList()
    {
        return new LinkedList<>(emojiMap.values());
    }

    public long getRoleCount()
    {
        return emojiMap.values().stream().map(ReactEmoji::getRoleMap).distinct().count();
    }

    public ReactEmoji getEmoji(String emoji)
    {
        return emojiMap.getOrDefault(emoji, null);
    }

    public ReactEmoji getEmoji(ReactionEmoji emoji)
    {
        return getEmoji(Util.emojiToString(emoji));
    }

    public void addEmoji(String emoji)
    {
        emojiMap.put(emoji, new ReactEmoji(emoji));
    }

    public boolean hasEmoji(String emoji)
    {
        return emojiMap.containsKey(emoji);
    }

    public boolean hasEmoji(ReactionEmoji emoji)
    {
        return hasEmoji(Util.emojiToString(emoji));
    }

    public ReactEmoji getOrAddEmoji(String emoji)
    {
        if (!emojiMap.containsKey(emoji))
            addEmoji(emoji);
        return getEmoji(emoji);
    }

    public ReactEmoji getOrAddEmoji(ReactionEmoji emoji)
    {
        return getOrAddEmoji(Util.emojiToString(emoji));
    }

    public void removeEmoji(ReactionEmoji emoji)
    {
        emojiMap.remove(Util.emojiToString(emoji));
    }
}

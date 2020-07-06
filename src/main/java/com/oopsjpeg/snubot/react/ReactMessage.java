package com.oopsjpeg.snubot.react;

import com.oopsjpeg.snubot.data.DiscordData;
import com.oopsjpeg.snubot.data.ChildData;
import com.oopsjpeg.snubot.data.SaveData;
import com.oopsjpeg.snubot.util.Util;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ReactMessage extends DiscordData implements ChildData<ReactManager>, SaveData
{
    private final String channelId;
    private final Map<String, ReactEmoji> emojiMap = new HashMap<>();

    private transient ReactManager parent;
    private transient boolean markedForSave;

    public ReactMessage(final String id, final String channelId)
    {
        super(id);
        this.channelId = channelId;
    }

    public Mono<Message> getMessage()
    {
        return parent.getParent().getGateway().getMessageById(getChannelIdAsSnowflake(), getIdAsSnowflake());
    }

    public String getChannelId()
    {
        return channelId;
    }

    public Snowflake getChannelIdAsSnowflake()
    {
        return Snowflake.of(channelId);
    }

    public Map<String, ReactEmoji> getEmojiMap()
    {
        return emojiMap;
    }

    public List<ReactEmoji> getEmojiList()
    {
        return new LinkedList<>(emojiMap.values());
    }

    public int getEmojiCount()
    {
        return emojiMap.size();
    }

    public long getRoleCount()
    {
        return emojiMap.values().stream().map(ReactEmoji::getRoleMap).distinct().count();
    }

    public ReactEmoji getEmoji(String emoji)
    {
        return (ReactEmoji) emojiMap.get(emoji).parent(this);
    }

    public ReactEmoji getEmoji(ReactionEmoji emoji)
    {
        return getEmoji(Util.emojiToString(emoji));
    }

    public ReactEmoji addEmoji(String emoji)
    {
        emojiMap.put(emoji, new ReactEmoji(emoji));
        return getEmoji(emoji);
    }

    public ReactEmoji addEmoji(ReactionEmoji emoji)
    {
        return addEmoji(Util.emojiToString(emoji));
    }

    public void removeEmoji(String emoji)
    {
        emojiMap.remove(emoji);
    }

    public void removeEmoji(ReactionEmoji emoji)
    {
        removeEmoji(Util.emojiToString(emoji));
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

    @Override
    public ReactManager getParent()
    {
        return parent;
    }

    @Override
    public void setParent(ReactManager parent)
    {
        this.parent = parent;
    }

    @Override
    public void setMarkedForSave(boolean markedForSave)
    {
        this.markedForSave = markedForSave;
    }

    @Override
    public boolean isMarkedForSave()
    {
        return markedForSave;
    }
}

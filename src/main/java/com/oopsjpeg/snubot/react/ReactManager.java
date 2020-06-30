package com.oopsjpeg.snubot.react;

import com.oopsjpeg.snubot.util.Util;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.User;
import discord4j.core.object.reaction.ReactionEmoji;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static com.oopsjpeg.snubot.react.ReactRole.Type.ONCE;

public class ReactManager
{
    private Map<String, ReactContainer> containerMap = new HashMap<>();

    public void onReactAdd(ReactionAddEvent event)
    {
        Message message = event.getMessage().block();
        User user = event.getUser().block();
        // Check if the message has a container
        if (!user.isBot() && hasContainer(message))
        {
            ReactContainer container = getContainer(message);
            ReactionEmoji emoji = event.getEmoji();
            String emojiStr = Util.emojiToString(emoji);
            // Check if the container has this emoji
            if (container.getReactionMap().containsKey(emojiStr))
            {
                // Give the reaction's roles to the user
                ReactReaction emote = container.getReaction(emojiStr);
                Member member = event.getMember().get();
                emote.getRoleMap().values().forEach(role -> member.addRole(Snowflake.of(role.getId())).block());
            }
        }
    }

    public void onReactRemove(ReactionRemoveEvent event)
    {
        Message message = event.getMessage().block();
        User user = event.getUser().block();
        // Check if the message has a container
        if (!user.isBot() && hasContainer(message))
        {
            ReactContainer container = getContainer(message);
            ReactionEmoji emoji = event.getEmoji();
            String emojiStr = Util.emojiToString(emoji);
            // Check if the container has this emoji
            if (container.getReactionMap().containsKey(emojiStr))
            {
                // Remove the reaction's roles from the user
                ReactReaction emote = container.getReaction(emojiStr);
                Member member = user.asMember(event.getGuildId().get()).block();
                emote.getRoleMap().values().stream()
                        .filter(role -> role.getType() != ONCE)
                        .forEach(role -> member.removeRole(Snowflake.of(role.getId())).block());
            }
        }
    }

    public Map<String, ReactContainer> getContainerMap()
    {
        return containerMap;
    }

    public void setContainerMap(Map<String, ReactContainer> containerMap)
    {
        this.containerMap = containerMap;
    }

    public ReactContainer getContainer(Message message)
    {
        return getContainerMap().get(message.getId().asString());
    }

    public ReactContainer getOrAddContainer(Message message)
    {
        if (!hasContainer(message))
            addContainer(message);
        return getContainer(message);
    }

    public ReactContainer addContainer(Message message)
    {
        return containerMap.put(message.getId().asString(), new ReactContainer(message.getId().asString()));
    }

    public ReactContainer removeContainer(Message message)
    {
        return containerMap.remove(message.getId().asString());
    }

    public boolean hasContainer(Message message)
    {
        return containerMap.containsKey(message.getId().asString());
    }

    public void updateContainer(Message message)
    {
        if (hasContainer(message))
        {
            ReactContainer container = getContainer(message);
            // Add each reaction in the container to the message
            container.getReactionMap().values().forEach(emote -> message.addReaction(Util.stringToEmoji(emote.getEmoji())).block());
            // Add "missed" reactions, anything the bot somehow didn't catch
            Snowflake guildId = message.getGuild().block().getId();
            container.getReactionMap().values().forEach(emote -> emote.getRoleMap().values().stream()
                    .map(role -> Snowflake.of(role.getId()))
                    .forEach(roleId -> message.getReactors(Util.stringToEmoji(emote.getEmoji()))
                            .flatMap(user -> user.asMember(guildId))
                            .filter(user -> !user.getRoleIds().contains(roleId))
                            .map(user -> user.addRole(roleId))
                            .subscribe()));
        }
    }

    public void addRoleToEmoji(Message message, ReactionEmoji emoji, Role role, ReactRole.Type type)
    {
        getOrAddContainer(message).getOrAddReaction(Util.emojiToString(emoji)).addRole(role.getId().asString(), type);

        message.addReaction(emoji).block();
    }

    public void removeRoleFromEmoji(Message message, ReactionEmoji emoji, Role role)
    {
        if (hasContainer(message))
        {
            String emojiStr = Util.emojiToString(emoji);
            ReactContainer container = getContainer(message);
            ReactReaction emote = container.getReaction(emojiStr);

            emote.getRoleMap().remove(role.getId().asString());

            if (emote.getRoleMap().isEmpty())
            {
                container.getReactionMap().remove(emojiStr);
                message.removeReactions(emoji).block();
            }

            if (container.getReactionMap().isEmpty())
            {
                removeContainer(message);
            }
        }
    }

    public void removeRoleFromAll(Message message, Role role)
    {
        if (hasContainer(message))
        {
            for (ReactReaction reaction : new LinkedList<>(getContainer(message).getReactionMap().values()))
                removeRoleFromEmoji(message, Util.stringToEmoji(reaction.getEmoji()), role);
        }
    }
}

package com.oopsjpeg.snubot.react;

import com.oopsjpeg.snubot.util.Util;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.User;
import discord4j.core.object.reaction.ReactionEmoji;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.oopsjpeg.snubot.react.ReactRole.Type.ONCE;

public class ReactController
{
    private List<ReactContainer> containerList = new ArrayList<>();

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
            if (container.hasReaction(emojiStr))
            {
                // Give the reaction's roles to the user
                ReactReaction reaction = container.getReaction(emojiStr);
                Member member = event.getMember().get();
                reaction.getRoleList().forEach(role -> member.addRole(role.getSnowflake()).block());
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
            if (container.hasReaction(emojiStr))
            {
                // Remove the reaction's roles from the user
                ReactReaction reaction = container.getReaction(emojiStr);
                Member member = user.asMember(event.getGuildId().get()).block();
                reaction.getRoleList().stream()
                        .filter(role -> role.getType() != ONCE)
                        .forEach(role -> member.removeRole(role.getSnowflake()).block());
            }
        }
    }

    public List<ReactContainer> getContainerList()
    {
        return containerList;
    }

    public void setContainerList(List<ReactContainer> containerList)
    {
        this.containerList = containerList;
    }

    public ReactContainer getContainer(Message message)
    {
        return containerList.stream().filter(container -> container.getId().equals(message.getId().asString())).findAny().orElse(null);
    }

    public ReactContainer getOrAddContainer(Message message) {
        if (!hasContainer(message))
            addContainer(message);
        return getContainer(message);
    }

    public void addContainer(Message message) {
        if (hasContainer(message))
            removeContainer(message);
        containerList.add(new ReactContainer(message.getId().asString()));
    }

    public void removeContainer(Message message) {
        containerList.removeIf(container -> container.getId().equals(message.getId().asString()));
    }

    public boolean hasContainer(Message message)
    {
        return containerList.stream().anyMatch(container -> container.getId().equals(message.getId().asString()));
    }

    public void updateContainer(Message message) {
        message.removeAllReactions().block();
        if (hasContainer(message))
        {
            ReactContainer container = getContainer(message);
            // Add each reaction in the container
            container.getReactionList().forEach(reaction -> message.addReaction(Util.stringToEmoji(reaction.getEmoji())).block());
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
            ReactReaction reaction = container.getReaction(emojiStr);

            reaction.removeRole(role.getId().asString());

            if (reaction.getRoleList().isEmpty())
            {
                container.removeReaction(emojiStr);
                message.removeReactions(emoji).block();
            }

            if (container.getReactionList().isEmpty())
            {
                removeContainer(message);
            }
        }
    }

    public void removeRoleFromAll(Message message, Role role)
    {
        if (hasContainer(message))
        {
            for (ReactReaction reaction : new LinkedList<>(getContainer(message).getReactionList()))
                removeRoleFromEmoji(message, Util.stringToEmoji(reaction.getEmoji()), role);
        }
    }
}

package com.oopsjpeg.snubot.util;

import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;

import java.util.function.Consumer;

public class ChatUtil
{
    public static String formatUser(User u)
    {
        return u.getUsername() + "#" + u.getDiscriminator();
    }

    public static Consumer<EmbedCreateSpec> user(User user)
    {
        return e -> e.setAuthor(user.getUsername() + "#" + user.getDiscriminator(), null, user.getAvatarUrl());
    }

    public static Consumer<EmbedCreateSpec> error(User user, String content)
    {
        return user(user).andThen(e -> e.setColor(Color.RED).setDescription(":x: " + content));
    }

    public static Consumer<EmbedCreateSpec> info(User user, String content)
    {
        return user(user).andThen(e -> e.setColor(Color.CYAN).setDescription(content));
    }

    public static Consumer<EmbedCreateSpec> success(User user, String content)
    {
        return user(user).andThen(e -> e.setColor(Color.GREEN).setDescription(content));
    }
}

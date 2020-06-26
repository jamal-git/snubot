package com.oopsjpeg.snubot.util;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.EmbedCreateSpec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class Util
{
    public static String emojiToString(ReactionEmoji emoji)
    {
        return emoji.asCustomEmoji()
                .map(e -> "<:" + e.getName() + ":" + e.getId().asString() + ">")
                .orElse(emoji.asUnicodeEmoji()
                        .map(ReactionEmoji.Unicode::getRaw)
                        .orElse(null));
    }

    public static ReactionEmoji stringToEmoji(String string)
    {
        if (string.matches("<a?:.*:\\d+>"))
        {
            String[] split = string.replaceAll("([<>])", "").split(":");
            return ReactionEmoji.custom(Snowflake.of(split[2]), split[1], split[0].equals("a"));
        }
        else
        {
            return ReactionEmoji.unicode(string);
        }
    }

    public static boolean searchString(String s1, String s2)
    {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();
        return s1.equals(s2) || s1.contains(s2) || s2.contains(s1);
    }

    public static String[] buildArguments(String s)
    {
        String[] split = s.split(" ");
        // Toss the first index, it's the alias
        List<String> base = Arrays.asList(Arrays.copyOfRange(split, 1, split.length));
        List<String> args = new ArrayList<>();

        boolean next = true;
        for (int i = 0; i < base.size(); i++)
        {
            String value = base.get(i);

            if (next) args.add(value.replaceAll("\"", ""));
            else args.set(args.size() - 1, args.get(args.size() - 1) + " " + value.replaceAll("\"", ""));

            // If value starts with " and can be matched
            if (value.startsWith("\"") && base.subList(i + 1, base.size()).stream().anyMatch(a -> a.contains("\"")))
                next = false;
            // If value ends with "
            if (value.endsWith("\""))
                next = true;
        }

        return args.toArray(new String[0]);
    }

    public static boolean isDigits(String s) {
        return s.matches("-?\\d+(\\.\\d+)?");
    }

    public static String formatUser(User u) {
        return u.getUsername() + "#" + u.getDiscriminator();
    }

    public static Consumer<EmbedCreateSpec> embed(User user)
    {
        return e -> e.setAuthor(user.getUsername() + "#" + user.getDiscriminator(), null, user.getAvatarUrl());
    }

    public static Message send(MessageChannel channel, User user, String content)
    {
        return channel.createEmbed(embed(user).andThen(e -> e.setDescription(content))).block();
    }
}

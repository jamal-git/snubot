package com.oopsjpeg.snubot.util;

import com.oopsjpeg.snubot.data.impl.MemberData;
import discord4j.core.object.entity.Member;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;

import java.util.function.Consumer;

public class Embeds
{
    public static Consumer<EmbedCreateSpec> profile(MemberData data)
    {
        Member member = data.discord().block();
        return ChatUtil.authorUser(data.discord().block()).andThen(e ->
        {
            e.setColor(Color.CYAN);
            e.setThumbnail(member.getAvatarUrl());

            String description = "Level **" + (data.getLevel() + 1) + "** (" + Util.comma(data.getXp()) + " / " + Util.comma(data.getMaxXp()) + ")";
            description += "\nCoins: **" + Util.comma(data.getCoins()) + "**";
            e.setDescription(description);
        });
    }
}

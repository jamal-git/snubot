package com.oopsjpeg.snubot.manager.impl;

import com.oopsjpeg.snubot.Snubot;
import com.oopsjpeg.snubot.data.impl.GuildData;
import com.oopsjpeg.snubot.data.impl.MemberData;
import com.oopsjpeg.snubot.manager.Manager;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;

public class LevelManager implements Manager
{
    private final Snubot parent;

    public LevelManager(Snubot parent)
    {
        this.parent = parent;
    }

    public void onMessage(MessageCreateEvent event)
    {
        GatewayDiscordClient client = event.getClient();
        Message message = event.getMessage();
        MessageChannel channel = message.getChannel().block();
        Guild guild = message.getGuild().block();
        User author = message.getAuthor().orElse(null);

        if (author != null && guild != null && !author.isBot())
        {
            GuildData guildData = parent.getOrAddGuildData(guild);
            if (guildData != null)
            {
                MemberData memberData = guildData.getOrAddMemberData(author);
                // Give message XP if possible
                if (memberData.messageXp())
                {
                    guildData.markForSave();
                    // Check for level ups
                    if (memberData.levelUp())
                    {
                        // Update level-based roles when they level up
                        guildData.getLeveling().syncRoles(author);
                    }

                }
            }
        }
    }

    @Override
    public Snubot getParent()
    {
        return parent;
    }

    @Override
    public void register(GatewayDiscordClient gateway)
    {
        gateway.on(MessageCreateEvent.class).subscribe(this::onMessage);
    }
}

package com.oopsjpeg.snubot.manager;

import com.oopsjpeg.snubot.Manager;
import com.oopsjpeg.snubot.Snubot;
import com.oopsjpeg.snubot.data.impl.GuildData;
import com.oopsjpeg.snubot.data.impl.MemberData;
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
                // Give message rewards if possible
                if (memberData.message()) guildData.markForSave();
            }
        }
    }

    @Override
    public void register(GatewayDiscordClient gateway)
    {
        gateway.on(MessageCreateEvent.class).subscribe(this::onMessage);
    }

    @Override
    public Snubot getParent()
    {
        return parent;
    }
}

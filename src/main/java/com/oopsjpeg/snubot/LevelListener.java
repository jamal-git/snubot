package com.oopsjpeg.snubot;

import com.oopsjpeg.snubot.data.GuildData;
import com.oopsjpeg.snubot.data.MemberData;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;

public class LevelListener
{
    public void onMessage(MessageCreateEvent event)
    {
        GatewayDiscordClient client = event.getClient();
        Message message = event.getMessage();
        MessageChannel channel = message.getChannel().block();
        Guild guild = message.getGuild().block();
        User author = message.getAuthor().orElse(null);

        if (author != null && channel != null && guild != null && !author.isBot())
        {
            GuildData guildData = Snubot.getInstance().getGuildData(guild.getId().asString());
            if (guildData != null && guildData.isLevelingEnabled())
            {
                MemberData memberData = guildData.getOrAddMemberData(author.getId().asString());
                if (memberData.messageXp()) guildData.updateLeveledRoles(author.asMember(guild.getId()).block());
            }
        }
    }
}

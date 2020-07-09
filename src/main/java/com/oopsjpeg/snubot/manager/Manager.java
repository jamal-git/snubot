package com.oopsjpeg.snubot.manager;

import com.oopsjpeg.snubot.Snubot;
import discord4j.core.GatewayDiscordClient;

public interface Manager
{
    default void register(GatewayDiscordClient gateway) {}

    Snubot getParent();
}

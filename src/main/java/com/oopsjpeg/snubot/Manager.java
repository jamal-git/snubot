package com.oopsjpeg.snubot;

import discord4j.core.GatewayDiscordClient;

public interface Manager
{
    default void register(GatewayDiscordClient gateway) {}

    Snubot getParent();
}

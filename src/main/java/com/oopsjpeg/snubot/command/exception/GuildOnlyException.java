package com.oopsjpeg.snubot.command.exception;

public class GuildOnlyException extends CommandException
{
    public GuildOnlyException()
    {
        super("This command must be used in a guild.");
    }
}

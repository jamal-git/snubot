package com.oopsjpeg.snubot.command.exception;

public class DeveloperOnlyException extends CommandException
{
    public DeveloperOnlyException()
    {
        super("You must be the developer to use this command.");
    }
}

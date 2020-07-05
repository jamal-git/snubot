package com.oopsjpeg.snubot.command.exception;

public class ModOnlyException extends CommandException
{
    public ModOnlyException()
    {
        super("You must be a moderator to use this command.");
    }
}

package com.oopsjpeg.snubot.command.exception;

import com.oopsjpeg.snubot.command.Command;
import com.oopsjpeg.snubot.command.CommandRegistry;

public class InvalidUsageException extends CommandException
{
    public InvalidUsageException(Command command, CommandRegistry registry, String message)
    {
        super("Correct usage: `" + registry.format(command) + " " + message + "`");
    }
}

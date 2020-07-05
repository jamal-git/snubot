package com.oopsjpeg.snubot.command.exception;

public class PermissionException extends CommandException
{
    public PermissionException()
    {
        super("You do not have the permission(s) to use this command.");
    }
}

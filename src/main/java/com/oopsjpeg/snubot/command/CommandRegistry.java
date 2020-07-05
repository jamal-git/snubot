package com.oopsjpeg.snubot.command;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CommandRegistry
{
    private final String prefix;
    private final Set<Command> commandSet = new HashSet<>();

    public CommandRegistry(final String prefix)
    {
        this.prefix = prefix.toLowerCase();
    }

    public Command find(String alias)
    {
        return commandSet.stream()
                .filter(c -> Arrays.stream(c.getAliases())
                        .anyMatch(a -> a.equalsIgnoreCase(alias)))
                .findAny().orElse(null);
    }

    public String format(Command command)
    {
        return prefix + command.getAliases()[0];
    }

    public String getPrefix()
    {
        return prefix;
    }

    public Set<Command> getCommandSet()
    {
        return commandSet;
    }
}

package com.oopsjpeg.snubot.command;

import java.util.Arrays;
import java.util.HashSet;

public class CommandRegistry extends HashSet<Command>
{
    private final String prefix;

    public CommandRegistry(final String prefix)
    {
        this.prefix = prefix.toLowerCase();
    }

    public Command find(String alias)
    {
        return stream()
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
}

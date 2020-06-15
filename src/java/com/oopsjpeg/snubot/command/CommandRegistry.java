package com.oopsjpeg.snubot.command;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CommandRegistry {
    private final Set<Command> commands = new HashSet<>();

    public Set<Command> getCommands() {
        return commands;
    }

    public Command find(String alias) {
        return commands.stream()
                .filter(c -> Arrays.stream(c.getAliases())
                        .anyMatch(a -> a.equalsIgnoreCase(alias)))
                .findAny().orElse(null);
    }
}

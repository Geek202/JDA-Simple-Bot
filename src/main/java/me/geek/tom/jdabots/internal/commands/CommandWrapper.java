package me.geek.tom.jdabots.internal.commands;

import me.geek.tom.jdabots.api.command.ICommand;

public class CommandWrapper {

    private final ICommand command;
    private final String name;
    private final String description;

    public CommandWrapper(ICommand command, String name, String description) {
        this.command = command;
        this.name = name;
        this.description = description;
    }

    public ICommand getCommand() {
        return command;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}

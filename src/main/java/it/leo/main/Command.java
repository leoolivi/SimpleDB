package it.leo.main;

import java.util.Optional;

public class Command {
    private String name;
    private Optional<Command[]> subCommands;
    private boolean hasArg;
    private String description;

    public Command(String name, boolean hasArg, Optional<Command[]> subCommands, String description) {
        this.name = name;
        this.hasArg = hasArg;
        this.description = description;
        this.subCommands = subCommands;
    }

    public Command(String name, boolean hasArg,  String description) {
        this.name = name;
        this.hasArg = hasArg;
        this.description = description;
        this.subCommands = Optional.empty();
    }
    
    public Command(String name, Command[] subCommands) {
        this.name = name;
        this.subCommands = Optional.of(subCommands);
    }

    public Command(String name) {
        this.name = name;
        this.subCommands = Optional.empty();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Optional<Command[]> getSubCommands() {
        return subCommands;
    }

    public void setSubCommands(Command[] subCommands) {
        this.subCommands = Optional.of(subCommands);
    }


    public boolean isHasArg() {
        return hasArg;
    }

    public void setHasArg(boolean hasArg) {
        this.hasArg = hasArg;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}

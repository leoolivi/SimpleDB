package it.leo.main.factories;

import java.util.HashMap;
import java.util.Map;

import it.leo.main.Command;
import it.leo.main.exceptions.InvalidCommandException;

public class CommandFactory {

    private final Map<String, Integer> commandsLengths = new HashMap<>();

    public CommandFactory() {
        commandsLengths.put("GET", 2);
        commandsLengths.put("SET", 4);
        commandsLengths.put("GETALL", 1);
    }

    public Command getCommands(String name) throws InvalidCommandException {
        Command command = new Command(name);
        switch (name) {
            case "GET" -> {
                command.setDescription("Get a value from key. Example: GET key");
                command.setHasArg(true);
            }
            case "SET" -> {
                command.setDescription("Set a key with value. Example: SET key TO value");
                command.setHasArg(true);
                Command[] subCommands = {new Command("TO", true, "Indicates the value of the key. See SET")};
                command.setSubCommands(subCommands);
            }
            case "GETALL" -> {
                command.setDescription("Get all values. Example: GETALL");
                command.setHasArg(false);
            }
            default -> command = new Command("", new Command[0]); 
        }
        return command;
    }

    public Map<String, Integer> getCommandsLengths() {
        return commandsLengths;
    }
}

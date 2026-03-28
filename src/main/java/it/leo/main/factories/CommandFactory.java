package it.leo.main.factories;

import java.util.List;

import it.leo.main.Command;

public class CommandFactory {

    public static final Command GET = Command.builder().setName("GET")
        .setHasArg(true)
        .setDescription("Get a value from key. Example: GET key")
        .build();

    public static final Command GETALL = Command.builder().setName("GETALL")
        .setHasArg(false)
        .setDescription("Get all values. Example: GETALL")
        .build();

    public static final Command SET = Command.builder().setName("SET")
        .setHasArg(true)
        .setDescription("Set a key with value. Example: SET key TO value")
        .setSubCommands(List.of(Command.builder()
            .setName("TO")
            .setHasArg(true)
            .setDescription("Indicates the value")
            .build()))
        .build();

    public static final List<Command> ALL = List.of(GET, SET, GETALL);
}

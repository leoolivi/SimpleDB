package it.leo.main.protocol;

import java.util.List;

public class QueryCommandFactory {

    public static final QueryCommand GET = QueryCommand.builder().setName("GET")
        .setHasArg(true)
        .setDescription("Get a value from key. Example: GET key")
        .build();

    public static final QueryCommand GETALL = QueryCommand.builder().setName("GETALL")
        .setHasArg(false)
        .setDescription("Get all values. Example: GETALL")
        .build();

    public static final QueryCommand SET = QueryCommand.builder().setName("SET")
        .setHasArg(true)
        .setDescription("Set a key with value. Example: SET key TO value")
        .setSubCommands(List.of(QueryCommand.builder()
            .setName("TO")
            .setHasArg(true)
            .setDescription("Indicates the value")
            .build()))
        .build();

    public static final List<QueryCommand> ALL = List.of(GET, SET, GETALL);
}

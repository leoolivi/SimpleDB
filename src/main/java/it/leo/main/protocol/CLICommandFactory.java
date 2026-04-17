package it.leo.main.protocol;

import java.util.List;

public class CLICommandFactory {
    public static final CLICommand HELP = new CLICommand("help", "Shows this help message");
    public static final CLICommand CONNECT = new CLICommand("connect", "Connects to the database. Supports a string  with username and password");
    public static final CLICommand EXIT = new CLICommand("exit", "Exits the program");
    public static final CLICommand QUERY  = new CLICommand("query", "Accepts a query and queries the db. Only if the connection is estabilished");
    public static final CLICommand STATUS  = new CLICommand("status", "Shows the status of the connection to the db: CONNECTED, NOT CONNECTED");
    public static final List<CLICommand> ALL = List.of(HELP, CONNECT, EXIT, QUERY, STATUS);
}

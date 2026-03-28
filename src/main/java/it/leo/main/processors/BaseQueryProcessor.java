package it.leo.main.processors;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import it.leo.main.Command;
import it.leo.main.exceptions.InvalidQueryException;
import it.leo.main.factories.CommandFactory;
import it.leo.main.processors.interfaces.QueryProcessor;

public class BaseQueryProcessor implements QueryProcessor {

    private final CommandFactory commandFactory;

    public BaseQueryProcessor(CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

    @Override
    public List<String> processQuery(String query) {
        // Validation of the query
        if (query.matches(".*[,./!@#$%^&*()_+{}\\[\\]\\\\|;:'\"/?<>].*")) throw new InvalidQueryException("Invalid character found");
        
        String[] queryChunksArr = query.split("[\s]"); // Splitting the string into chunks divided by spaces
        List<String> queryChunks = Arrays.stream(queryChunksArr).toList(); // Convert to List
        
        String commandStr = queryChunks.getFirst();
        Optional<Command> command = Optional.empty();
        for (Command c : CommandFactory.ALL) {
            command = c.getName().equals(commandStr) ? Optional.of(c) : command;  
        }

        if (command.isEmpty()) {
            return Collections.emptyList();
        }
        return queryChunks.subList(0, command.get().expectedTokens());
    }
}

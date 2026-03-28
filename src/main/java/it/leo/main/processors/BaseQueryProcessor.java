package it.leo.main.processors;

import java.util.Arrays;
import java.util.List;

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
        
        int i = 0;
        List<String> currChunk;
        Command currCommand;
        currCommand = commandFactory.getCommands(queryChunks.getFirst());

        switch (currCommand.getName()) {
            case "GET" -> currChunk = queryChunks.subList(i,i+2);
            case "SET" -> {
                if (queryChunks.size() == 4 && queryChunks.get(2).equals("TO")) { 
                    currChunk = queryChunks.subList(i, i+4); 
                } else {
                    throw new InvalidQueryException("SET command syntax not valid");
                }
            }
            case "GETALL" -> currChunk = queryChunks.subList(i, i+1);
            default -> currChunk = queryChunks.subList(i, i+1);
        }
        return currChunk;
    }
}

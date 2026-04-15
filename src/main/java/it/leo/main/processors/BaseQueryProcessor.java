package it.leo.main.processors;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import it.leo.main.QueryCommand;
import it.leo.main.exceptions.InvalidQueryException;
import it.leo.main.factories.QueryCommandFactory;
import it.leo.main.processors.interfaces.QueryProcessor;

public class BaseQueryProcessor implements QueryProcessor {

    private final QueryCommandFactory QueryCommandFactory;

    public BaseQueryProcessor(QueryCommandFactory QueryCommandFactory) {
        this.QueryCommandFactory = QueryCommandFactory;
    }

    @Override
    public List<String> processQuery(String query) {
        // Validation of the query
        if (query.matches(".*[,./!@#$%^&*()_+{}\\[\\]\\\\|;:'\"/?<>].*")) throw new InvalidQueryException("Invalid character found");
        
        String[] queryChunksArr = query.split("[\s]"); // Splitting the string into chunks divided by spaces
        List<String> queryChunks = Arrays.stream(queryChunksArr).toList(); // Convert to List
        
        String commandStr = queryChunks.getFirst();
        Optional<QueryCommand> QueryCommand = Optional.empty();
        for (QueryCommand c : QueryCommandFactory.ALL) {
            QueryCommand = c.getName().equals(commandStr) ? Optional.of(c) : QueryCommand;  
        }

        if (QueryCommand.isEmpty()) {
            return Collections.emptyList();
        }
        return queryChunks.subList(0, QueryCommand.get().expectedTokens());
    }
}

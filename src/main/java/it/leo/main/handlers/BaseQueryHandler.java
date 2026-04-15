package it.leo.main.handlers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import it.leo.main.QueryCommand;
import it.leo.main.data.DBResponse;
import it.leo.main.data.enums.ResponseStatus;
import it.leo.main.factories.QueryCommandFactory;
import it.leo.main.handlers.interfaces.QueryHandler;
import it.leo.main.persistence.RecordRow;
import it.leo.main.persistence.interfaces.DBRepository;
import it.leo.main.persistence.interfaces.DBRow;
import it.leo.main.processors.interfaces.QueryProcessor;

public class BaseQueryHandler implements QueryHandler<String,String> {

    private final QueryProcessor queryProcessor;
    private final DBRepository<String, String> repository;

    public BaseQueryHandler(QueryProcessor queryProcessor, DBRepository<String, String> repository) {
        this.queryProcessor = queryProcessor;
        this.repository = repository;
    }

    @Override
    public DBResponse<String, String> handleQuery(String query) throws Exception {
        List<String> queryChunks = queryProcessor.processQuery(query);
        
        String commandStr = queryChunks.getFirst();
        Optional<QueryCommand> QueryCommand = Optional.empty();
        
        for (QueryCommand c: QueryCommandFactory.ALL) {
            QueryCommand = c.getName().equals(commandStr) ? Optional.of(c) : QueryCommand;
        }
        
        if (QueryCommand.isEmpty()) {
           return new DBResponse<>("Invalid QueryCommand "+commandStr, ResponseStatus.ERROR, Collections.emptyList()); 
        }

        String key;
        String value;
        String msg = "";
        ResponseStatus status = ResponseStatus.OK;
        List<DBRow<String, String>> rows;



        if (QueryCommand.get().expectedTokens() != queryChunks.size()) {
            msg = "Unmatching n. arguments for the QueryCommand "+commandStr+": expected "+QueryCommand.get().expectedTokens()+" got "+queryChunks.size();
            status = ResponseStatus.ERROR;
            return new DBResponse<>(msg, status, Collections.emptyList());
        }

        switch (commandStr) {
            case "GET" -> {
                key = queryChunks.get(1);
                var rowOptional = repository.findByKey(key);
                if (rowOptional.isPresent()) {
                    rows = List.of(rowOptional.get());
                    return new DBResponse<>(msg, status, rows);
                } else {
                   return new DBResponse<>("Row not found with key "+key, ResponseStatus.ERROR, Collections.emptyList()); 
                }
            }
            case "SET" -> {
                key = queryChunks.get(1);
                value = queryChunks.get(3);
                repository.set(key, value);
                rows = List.of(new RecordRow(key, value));
                return new DBResponse<>(msg, status, rows);
            }
            case "GETALL" -> {
                return new DBResponse<>(msg, status, repository.findAll());
            }
            default -> {
                return new DBResponse<>("Invalid QueryCommand "+QueryCommand, ResponseStatus.ERROR, Collections.emptyList());
            }
        }
    }

}

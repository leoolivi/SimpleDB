package it.leo.main.handlers;

import java.util.Collections;
import java.util.List;

import it.leo.main.data.DBResponse;
import it.leo.main.data.enums.ResponseStatus;
import it.leo.main.factories.CommandFactory;
import it.leo.main.handlers.interfaces.QueryHandler;
import it.leo.main.persistence.RecordRow;
import it.leo.main.persistence.interfaces.DBRepository;
import it.leo.main.persistence.interfaces.DBRow;
import it.leo.main.processors.interfaces.QueryProcessor;

public class BaseQueryHandler implements QueryHandler<String,String> {

    private final QueryProcessor queryProcessor;
    private final DBRepository<String, String> repository;
    private final CommandFactory commandFactory;

    public BaseQueryHandler(QueryProcessor queryProcessor, DBRepository<String, String> repository, CommandFactory commandFactory) {
        this.queryProcessor = queryProcessor;
        this.repository = repository;
        this.commandFactory = commandFactory;
    }

    @Override
    public DBResponse<String, String> handleQuery(String query) throws Exception {
        List<String> queryChunks = queryProcessor.processQuery(query);
        String command = queryChunks.getFirst();

        String msg = "";
        String key;
        String value;
        ResponseStatus status = ResponseStatus.OK;
        List<DBRow<String, String>> rows;

        if (!commandFactory.getCommandsLengths().containsKey(command)) {
           return new DBResponse<>("Invalid command "+command, ResponseStatus.ERROR, Collections.emptyList()); 
        }

        if (commandFactory.getCommandsLengths().get(command) != queryChunks.size()) {
            msg = "Unmatching n. arguments for the command "+command+": expected "+commandFactory.getCommandsLengths().get(command)+" got "+queryChunks.size();
            status = ResponseStatus.ERROR;
            return new DBResponse<>(msg, status, Collections.emptyList());
        }

        switch (command) {
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
                return new DBResponse<>("Invalid command "+command, ResponseStatus.ERROR, Collections.emptyList());
            }
        }
    }

}

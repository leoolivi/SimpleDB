package it.leo.main.processors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import it.leo.main.QueryCommand;
import it.leo.main.data.DBResponse;
import it.leo.main.data.enums.ResponseStatus;
import it.leo.main.data.tasks.GetAllRecordTask;
import it.leo.main.data.tasks.GetRecordTask;
import it.leo.main.data.tasks.SetRecordTask;
import it.leo.main.exceptions.InvalidQueryException;
import it.leo.main.factories.QueryCommandFactory;
import it.leo.main.persistence.interfaces.DBRepository;

public class PacketProcessor {
    private final BufferedReader bufferedReader;
    private final ThreadPoolExecutor threadPoolExecutor;
    private final PrintWriter printWriter;
    private final DBRepository dbRepository;

    public PacketProcessor(BufferedReader bufferedReader, PrintWriter printWriter, DBRepository dbRepository, ThreadPoolExecutor threadPoolExecutor) {
        this.bufferedReader = bufferedReader;
        this.printWriter = printWriter;
        this.dbRepository = dbRepository;
        this.threadPoolExecutor = threadPoolExecutor;
    }

    public DBResponse<String, String> processNextPacket() {
        try {
            System.out.println("DEBUG: In attesa di readLine...");
            String query = bufferedReader.readLine();
            System.out.println("DEBUG: Query ricevuta: " + query);
            // Validation of the query
            if (query.matches(".*[,./!@#$%^&*()_+{}\\[\\]\\\\|;:'\"/?<>].*")) throw new InvalidQueryException("Invalid character found");
            
            String[] queryChunksArr = query.split("[\s]"); // Splitting the string into chunks divided by spaces
            List<String> queryChunks = Arrays.stream(queryChunksArr).toList(); // Convert to List
            
            String commandStr = queryChunks.getFirst();
            System.out.println("DEBUG: first query command is " + commandStr);
            Optional<QueryCommand> QueryCommand = Optional.empty();
            
            for (QueryCommand c : QueryCommandFactory.ALL) {
                QueryCommand = c.getName().equals(commandStr) ? Optional.of(c) : QueryCommand;  
            }
            
            if (QueryCommand.isEmpty()) {
                System.out.println("DEBUG: Invalid command");
                return new DBResponse<>("Invalid QueryCommand "+commandStr, ResponseStatus.ERROR, Collections.emptyList()); 
            }

            queryChunks = queryChunks.subList(0, QueryCommand.get().expectedTokens());
            
            

            String key;
            String value;
            String msg;
            ResponseStatus status;

            if (QueryCommand.get().expectedTokens() != queryChunks.size()) {
                msg = "Unmatching n. arguments for the QueryCommand "+commandStr+": expected "+QueryCommand.get().expectedTokens()+" got "+queryChunks.size();
                status = ResponseStatus.ERROR;
                return new DBResponse<>(msg, status, Collections.emptyList());
            }

            switch (commandStr) {
                case "GET" -> {
                        key = queryChunks.get(1);
                        var task = new GetRecordTask(key, (DBRepository<String, String>) dbRepository);
                        var resFuture = threadPoolExecutor.submit(task);
                        var dbResponse = resFuture.get();
                        return dbResponse;
                    }
                case "SET" -> {
                    key = queryChunks.get(1);
                    value = queryChunks.get(3);
                    var task = new SetRecordTask(key, value, (DBRepository<String, String>) dbRepository);
                    var resFuture = threadPoolExecutor.submit(task);
                    var dbResponse = resFuture.get();
                    return dbResponse;
                }
                case "GETALL" -> {
                    var task = new GetAllRecordTask((DBRepository<String, String>) dbRepository);
                    var resFuture = threadPoolExecutor.submit(task);
                    var dbResponse = resFuture.get();
                    return dbResponse;
                }
                default -> {
                    System.out.println("DEBUG: Invalid command");
                    return new DBResponse<>("Invalid query command "+QueryCommand, ResponseStatus.ERROR, Collections.emptyList());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException | ExecutionException ex) {
            System.getLogger(PacketProcessor.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        } 
        return new DBResponse<>("Unexpected ERROR", ResponseStatus.ERROR, Collections.emptyList());
    }
}

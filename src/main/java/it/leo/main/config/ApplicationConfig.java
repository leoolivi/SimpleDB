package it.leo.main.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

import it.leo.main.Command;
import it.leo.main.factories.CommandFactory;
import it.leo.main.handlers.BaseQueryHandler;
import it.leo.main.handlers.interfaces.QueryHandler;
import it.leo.main.persistence.CsvRepository;
import it.leo.main.persistence.interfaces.DBRepository;
import it.leo.main.processors.BaseQueryProcessor;
import it.leo.main.processors.interfaces.QueryProcessor;

public class ApplicationConfig {

    // Globals
    public static final String SERVER_HOST = "localhost";
    public static final int SERVER_PORT = 9001;

    public static final String DB_FILE_PATH = "storage/";
    public static final String DB_FILENAME = "db.csv";

    // Core components
    private final CommandFactory commandFactory;
    private final DBRepository<String, String> repository;
    private final QueryProcessor queryProcessor;
    private final Scanner scanner;
    private final FileReader fileReader;
    private final BufferedReader bufferedReader;
    private final FileWriter fileWriter;
    private final BufferedWriter bufferedWriter;
    private final PrintWriter printWriter;
    
    // Dependencies
    private final QueryHandler<String, String> queryHandler;

    public ApplicationConfig() throws IOException {
        this.scanner = new Scanner(System.in);
        this.commandFactory = new CommandFactory();
        
        Path dbFilePath = Path.of(DB_FILE_PATH).resolve(DB_FILENAME);
        File dbFile = dbFilePath.toFile();
        if (!dbFile.exists()) {
            dbFile.createNewFile();
        }
        
        this.fileReader = new FileReader(dbFile);
        this.bufferedReader = new BufferedReader(fileReader);

        this.fileWriter = new FileWriter(dbFile, true);
        this.bufferedWriter = new BufferedWriter(fileWriter);
        this.printWriter = new PrintWriter(bufferedWriter, true);
        
        this.repository = new CsvRepository(dbFilePath);
        this.queryProcessor = new BaseQueryProcessor(commandFactory);
        this.queryHandler = new BaseQueryHandler(queryProcessor, repository, commandFactory);
    }

    public QueryHandler<String, String> getQueryHandler() {
        return queryHandler;
    }

    public Scanner getScanner() {
        return scanner;
    }

    public List<Command> getCommands() {
        return List.of(new Command("GET", true, "Get a value from key. Example: GET key"),
                    new Command("SET", true, "Set a key with value. Example: SET key TO value"),
                    new Command("GETALL", false, "Get all values. Example: GETALL"));
    }
}

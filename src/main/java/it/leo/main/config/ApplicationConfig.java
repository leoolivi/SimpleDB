package it.leo.main.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

import it.leo.main.protocol.QueryCommandFactory;
import it.leo.main.server.persistence.CsvRepository;
import it.leo.main.server.persistence.interfaces.DBRepository;

public class ApplicationConfig {

    // Globals
    public static final String SERVER_HOST = "localhost";
    public static final int SERVER_PORT = 9001;

    public static final String DB_FILE_PATH = "storage/";
    public static final String DB_FILENAME = "db.csv";
    public static final String SERVER_VERSION = "1.0";
    public static final String CHARSET = "UTF-8";
    public static final int MAX_THREAD_POOL_SIZE = 50;
    public static final int CORE_THREAD_POOL_SIZE = 20;
    public static final int MAX_WORKER_POOL_SIZE = 20;
    public static final int CORE_WORKER_POOL_SIZE = 10;
    public static final int CONNECTION_TIMEOUT = 10000;

    // Core components
    private final QueryCommandFactory QueryCommandFactory;
    private final DBRepository<String, String> repository;
    
    private final Scanner scanner;
    // Dependencies

    public ApplicationConfig() throws IOException {
        this.scanner = new Scanner(System.in);
        this.QueryCommandFactory = new QueryCommandFactory();
        
        Path dbFilePath = Path.of(DB_FILE_PATH).resolve(DB_FILENAME);
        File dbFile = dbFilePath.toFile();
        if (!dbFile.exists()) {
            dbFile.createNewFile();
        }
        
        this.repository = new CsvRepository(dbFilePath);
    }
    
    public Scanner getScanner() {
        return scanner;
    }
    
    public DBRepository<String, String> getRepository() {
        return repository;
    }
}

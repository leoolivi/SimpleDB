package it.leo.main.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import it.leo.main.exceptions.InvalidRowException;
import it.leo.main.persistence.interfaces.DBRepository;
import it.leo.main.persistence.interfaces.DBRow;

public class CsvRepository implements DBRepository<String, String>{

    private final Path dbFilePath;

    public CsvRepository(Path dbFilePath) {
        this.dbFilePath = dbFilePath;
    }
    
    @Override
    public List<DBRow<String, String>> findAll() throws IOException {
        try (var bufferedReader = new BufferedReader(new FileReader(dbFilePath.toFile()))) {
            Function<String, DBRow<String,String>> rowToRecordRow = row -> {
                String[] chunks = row.split("[,\n]");
                if (chunks.length != 2) throw new InvalidRowException("Invalid row");
                return new RecordRow(chunks[0], chunks[1]);
            };
            return bufferedReader.lines().map(rowToRecordRow).toList();
        }
    }

    @Override
    public Optional<DBRow<String, String>> findByKey(String key) throws IOException {
        try (var bufferedReader = new BufferedReader(new FileReader(dbFilePath.toFile()))) {
            DBRow<String, String> record = null;
            String[] chunks;
            Stream<String> lines = bufferedReader.lines();
            for (String line: lines.toArray(String[]::new)) {
                chunks = line.split("[,\n]");
                if (chunks[0].equals(key)) {
                    record = new RecordRow(chunks[0], chunks[1]); 
                }
            }
            return Optional.ofNullable(record);
        }
    }

    @Override
    public DBRow<String, String> set(String key, String value) throws IOException {
        Path tmpFile = Files.createTempFile(dbFilePath.getParent(), "db_temp", ".csv");
        try (BufferedReader buffReader = new BufferedReader(new FileReader(dbFilePath.toFile()));
            PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(tmpFile.toFile(), true)), true)) {
                var lines = buffReader.lines();
                lines.forEach(line -> {
                    String[] chunks = line.split("[,]");
                    if (!chunks[0].equals(key)) {
                        printWriter.println(String.format("%s,%s", chunks[0], chunks[1]));
                    }
                });
                printWriter.println(String.format("%s,%s",key,value));
        }
        Files.move(tmpFile, dbFilePath, StandardCopyOption.REPLACE_EXISTING);
        return new RecordRow(key, value);

    }
    
}

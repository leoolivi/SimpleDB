package it.leo.main.server.tasks;

import java.util.Collections;
import java.util.List;

import it.leo.main.server.DBResponse;
import it.leo.main.server.enums.ResponseStatus;
import it.leo.main.server.persistence.interfaces.DBRepository;
import it.leo.main.server.persistence.interfaces.DBRow;

public class GetRecordTask extends DBTask<String,String> {

    private final String key;

    public GetRecordTask(String key, DBRepository<String, String> repository) {
        super(repository);
        this.key = key;
    }

    @Override
    public DBResponse<String, String> call() throws Exception {
        try {
            var record = repository.findByKey(key);
            List<DBRow<String, String>> lines;

            if (record.isEmpty()) { 
                lines = Collections.emptyList(); 
            } else {
                lines = List.of(record.get());
            }

            return new DBResponse<>("", ResponseStatus.DATA, lines);
        } catch (Exception e) {
            e.printStackTrace();
            return new DBResponse<>("Error during GET operation", ResponseStatus.ERROR, Collections.emptyList());
        }
    }
    
}

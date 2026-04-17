package it.leo.main.server.tasks;

import java.util.Collections;
import java.util.List;

import it.leo.main.server.DBResponse;
import it.leo.main.server.enums.ResponseStatus;
import it.leo.main.server.persistence.RecordRow;
import it.leo.main.server.persistence.interfaces.DBRepository;

public class SetRecordTask extends DBTask<String,String> {

    private final String key;
    private final String value;

    public SetRecordTask(String key, String value, DBRepository<String, String> repository) {
        super(repository);
        this.key = key;
        this.value = value;
    }

    @Override
    public DBResponse<String, String> call() {
        try {
            repository.set(key, value);
            return new DBResponse<>("", ResponseStatus.DATA, List.of(new RecordRow(key, value)));
        } catch (Exception e) {
            e.printStackTrace();
            return new DBResponse<>("Error during SET operation", ResponseStatus.ERROR, Collections.emptyList());
        }
    }
    
}

package it.leo.main.data.tasks;

import java.util.Collections;

import it.leo.main.data.DBResponse;
import it.leo.main.data.enums.ResponseStatus;
import it.leo.main.persistence.interfaces.DBRepository;

public class GetAllRecordTask extends DBTask<String,String> {

    public GetAllRecordTask(DBRepository<String, String> repository) {
        super(repository);
    }

    @Override
    public DBResponse<String, String> call() throws Exception {
        try {
            var lines = repository.findAll();
            return new DBResponse<>("", ResponseStatus.DATA, lines);
        } catch (Exception e) {
            e.printStackTrace();
            return new DBResponse<>("Error during GETALL operation", ResponseStatus.ERROR, Collections.emptyList());
        }
    }
    
}
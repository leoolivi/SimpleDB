package it.leo.main.persistence;

import it.leo.main.persistence.interfaces.DBRow;

public class RecordRow extends DBRow<String, String> {
    public RecordRow(String key, String value) {
        super(key, value);
    }

}

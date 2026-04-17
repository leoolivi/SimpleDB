package it.leo.main.server.persistence;

import it.leo.main.server.persistence.interfaces.DBRow;

public class RecordRow extends DBRow<String, String> {
    public RecordRow(String key, String value) {
        super(key, value);
    }

}

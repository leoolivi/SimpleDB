package it.leo.main.handlers.interfaces;

import it.leo.main.data.DBResponse;

public interface QueryHandler<K,V> {
    public DBResponse<K, V> handleQuery(String query) throws Exception;
}

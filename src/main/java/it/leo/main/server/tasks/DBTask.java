package it.leo.main.server.tasks;

import java.util.concurrent.Callable;

import it.leo.main.server.DBResponse;
import it.leo.main.server.persistence.interfaces.DBRepository;

public abstract class DBTask<K, V> implements Callable<DBResponse<K, V>> {
    protected final DBRepository<K,V> repository;

    public DBTask(DBRepository<K, V> repository) {
        this.repository = repository;
    }

}

package it.leo.main.data.tasks;

import java.util.concurrent.Callable;

import it.leo.main.data.DBResponse;
import it.leo.main.persistence.interfaces.DBRepository;

public abstract class DBTask<K, V> implements Callable<DBResponse<K, V>> {
    protected final DBRepository<K,V> repository;

    public DBTask(DBRepository<K, V> repository) {
        this.repository = repository;
    }

}

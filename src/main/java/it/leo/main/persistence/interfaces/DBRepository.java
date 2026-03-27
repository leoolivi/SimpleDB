package it.leo.main.persistence.interfaces;

import java.util.List;
import java.util.Optional;

public interface DBRepository<K, V> {
    public List<DBRow<K, V>> findAll() throws Exception;
    public Optional<DBRow<K, V>> findByKey(K key) throws Exception;
    public DBRow<K, V> set(K key, V value) throws Exception;
}

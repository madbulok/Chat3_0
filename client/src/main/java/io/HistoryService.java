package io;

public interface HistoryService<T> {
    void save(T t);
    T load();
    void changeFile(String newFile);
}

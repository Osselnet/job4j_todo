package ru.job4j.todo.persistence;


import java.util.List;
import java.util.Optional;

public interface Store<T> {

    Optional<T> create(T type);

    Optional<T> findById(int id);

    boolean update(int id, T type);

    boolean delete(int id);

    List<T> findAll();

    List<T> findNew();

    List<T> findCompleted();
}
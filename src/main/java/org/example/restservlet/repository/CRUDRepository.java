package org.example.restservlet.repository;

import java.util.List;
import java.util.Optional;

public interface CRUDRepository <T, K> {

    List<T> findAll();

    Optional<T> findById(K id);

    T insert(T t);

    T update(T t);

    void deleteById(K id);
}

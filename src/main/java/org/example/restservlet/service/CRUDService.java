package org.example.restservlet.service;

import java.util.List;

public interface CRUDService <T> {

    List<T> findAll();

    T findById(Long id);

    T insert(T t);

    T update(Long id, T t);

    void deleteById(Long id);

}

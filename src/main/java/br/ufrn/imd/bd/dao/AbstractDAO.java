package br.ufrn.imd.bd.dao;

import java.util.List;

public interface AbstractDAO<T, ID> {
    T findById(ID id);
    List<T> findAll();
    void save(T entity);
    void update(T entity);
    void deleteById(ID id);
}


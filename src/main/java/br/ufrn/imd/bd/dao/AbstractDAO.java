package br.ufrn.imd.bd.dao;

import java.util.List;

public interface AbstractDAO<T, ID> {
    T buscarPorId(ID id);
    List<T> buscarTodos();
    T salvar(T entity);
    void atualizar(T entity);
    void deletarPorId(ID id);
}


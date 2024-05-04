package br.ufrn.imd.bd.dao;

import java.util.List;

public interface AbstractDAO<T, ID> {
    T buscarPorId(ID id);
    List<T> buscarTodos();
    T salvar(T entidade);
    void atualizar(T entidade);
    void deletarPorId(ID id);
}


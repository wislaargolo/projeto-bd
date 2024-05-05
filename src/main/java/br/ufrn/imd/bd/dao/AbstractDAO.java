package br.ufrn.imd.bd.dao;

import java.sql.SQLException;
import java.util.List;

public interface AbstractDAO<T, ID> {
    T buscarPorId(ID id) throws SQLException;
    List<T> buscarTodos();
    T salvar(T entidade) throws SQLException;
    void atualizar(T... entidade);
    void deletarPorId(ID id) throws SQLException;
}


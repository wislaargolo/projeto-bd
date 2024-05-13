package br.ufrn.imd.bd.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface AbstractDAO<T, ID> {
    T buscarPorId(ID id) throws SQLException;
    List<T> buscarTodos() throws SQLException;
    T salvar(Connection conn, T entidade) throws SQLException;
    void atualizar(Connection conn, T... entidade) throws SQLException;
    void deletarPorId(Connection conn, ID id) throws SQLException;
}


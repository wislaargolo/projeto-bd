package br.ufrn.imd.bd.dao;
import br.ufrn.imd.bd.connection.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDAO<T, ID>{
    protected Connection getConnection() throws SQLException {
        return DatabaseConfig.getConnection();
    }

    public abstract String getNomeTabela();

    protected String getBuscarTodosQuery() {
        return String.format("SELECT * FROM %s", getNomeTabela());
    }

    public List<T> buscarTodos() throws SQLException {
        List<T> resultados = new ArrayList<>();
        String sql = getBuscarTodosQuery();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                resultados.add(mapearResultado(rs));
            }
        }
        return resultados;
    }

    protected String getBuscarPorIdQuery() {
        return String.format("SELECT * FROM %s WHERE id = ?", getNomeTabela());
    }

    public T buscarPorId(ID id) throws SQLException {
        String sql = getBuscarPorIdQuery();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapearResultado(rs);
            }
        }
        return null;
    }

    public abstract T salvar(Connection conn, T entidade) throws SQLException;

    public abstract void atualizar(Connection conn, T... entidade) throws SQLException;

    public void deletarPorId(Connection conn, ID id) throws SQLException {
        String sql = String.format("DELETE FROM %s WHERE id = ?", getNomeTabela());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            stmt.executeUpdate();
        }
    }

    public abstract T mapearResultado(ResultSet rs) throws SQLException;
}


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
    public abstract T mapearResultado(ResultSet rs) throws SQLException;

    public T mapearResultado(ResultSet rs, String prefix) throws SQLException {
        return mapearResultado(rs, "");
    }


    public abstract String getNomeTabela();

    protected String getBuscarTodosQuery() {
        return String.format("SELECT * FROM %s WHERE is_ativo = true", getNomeTabela());
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
        return String.format("SELECT * FROM %s WHERE id" + "_" + getNomeTabela() + " = ?", getNomeTabela());
    }


    public T buscarPorChave(ID id) throws SQLException {
        String sql = getBuscarPorIdQuery();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                return mapearResultado(rs);
            }
        }
        return null;
    }

    public abstract T salvar(Connection conn, T entidade) throws SQLException;

    public abstract void atualizar(Connection conn, T... entidade) throws SQLException;

    public void deletar(Connection conn, Long id) throws SQLException {
        String sql = String.format(
                "UPDATE %s SET is_ativo = false WHERE id" + "_" + getNomeTabela() + " = ?",
                getNomeTabela()
        );
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            stmt.executeUpdate();
        }
    }
}


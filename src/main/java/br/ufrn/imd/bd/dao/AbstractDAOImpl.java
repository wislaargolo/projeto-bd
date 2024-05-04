package br.ufrn.imd.bd.dao;
import br.ufrn.imd.bd.connection.DatabaseConfig;
import br.ufrn.imd.bd.model.Caixa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDAOImpl<T, ID> implements AbstractDAO<T, ID> {
    protected Connection getConnection() throws SQLException {
        return DatabaseConfig.getConnection();
    }
    protected abstract T mapResult(ResultSet rs) throws SQLException;

    public abstract String getTableName();

    @Override
    public List<T> findAll() {
        List<T> resultados = new ArrayList<>();
        String sql = String.format("SELECT * FROM %s", getTableName());
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                resultados.add(mapResult(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultados;
    }

    @Override
    public T findById(ID id) {
        String sql = String.format("SELECT * FROM %s WHERE id = ?", getTableName());
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResult(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteById(ID id) {
        String sql = String.format("DELETE FROM %s WHERE id = ?", getTableName());
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}


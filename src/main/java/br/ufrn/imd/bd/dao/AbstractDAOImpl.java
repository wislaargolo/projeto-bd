package br.ufrn.imd.bd.dao;
import br.ufrn.imd.bd.connection.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class AbstractDAOImpl<T, ID> implements AbstractDAO<T, ID> {
    protected Connection getConnection() throws SQLException {
        return DatabaseConfig.getConnection();
    }
    protected abstract T mapRow(ResultSet rs) throws SQLException;

    public abstract String getTableName();

    @Override
    public T findById(ID id) {
        String sql = String.format("SELECT * FROM %s WHERE id = ?", getTableName());
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
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


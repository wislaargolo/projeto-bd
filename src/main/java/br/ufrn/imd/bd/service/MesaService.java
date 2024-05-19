package br.ufrn.imd.bd.service;

import br.ufrn.imd.bd.connection.DatabaseConfig;
import br.ufrn.imd.bd.dao.MesaDAO;
import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.model.Mesa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
public class MesaService {

    @Autowired
    private MesaDAO mesaDAO;

    public List<Mesa> buscarTodos() throws SQLException {
        return mesaDAO.buscarTodos();
    }

    public Mesa buscarPorId(Long id) throws SQLException {
        return mesaDAO.buscarPorId(id);
    }

    public Mesa salvar(Mesa mesa) throws SQLException, EntidadeJaExisteException {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            mesa = mesaDAO.salvar(conn, mesa);
            conn.commit();
        } catch (SQLException e) {
            DatabaseConfig.rollback(conn);
            throw e;
        } finally {
            DatabaseConfig.close(conn);
        }

        return mesa;
    }

    public void atualizar(Mesa mesa) throws EntidadeJaExisteException, SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            mesaDAO.atualizar(conn, mesa);
            conn.commit();
        } catch (SQLException e) {
            DatabaseConfig.rollback(conn);
            throw e;
        } finally {
            DatabaseConfig.close(conn);
        }
    }

    public void deletarPorId(Long id) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            mesaDAO.deletarPorId(conn, id);
            conn.commit();
        } catch (SQLException e) {
            DatabaseConfig.rollback(conn);
            throw e;
        } finally {
            DatabaseConfig.close(conn);
        }
    }
}

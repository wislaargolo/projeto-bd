package br.ufrn.imd.bd.service;

import br.ufrn.imd.bd.connection.DatabaseConfig;
import br.ufrn.imd.bd.dao.CozinheiroDAO;
import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.model.Cozinheiro;
import br.ufrn.imd.bd.validation.FuncionarioValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
public class CozinheiroService {

    @Autowired
    private CozinheiroDAO cozinheiroDAO;

    @Autowired
    private FuncionarioValidator funcionarioValidator;

    public List<Cozinheiro> buscarTodos() throws SQLException {
        return cozinheiroDAO.buscarTodos();
    }

    public Cozinheiro buscarPorId(Long id) throws SQLException {
        return cozinheiroDAO.buscarPorId(id);
    }

    public Cozinheiro salvar(Cozinheiro cozinheiro) throws SQLException, EntidadeJaExisteException {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            try {
                funcionarioValidator.validar(conn, cozinheiro);
            } catch (EntidadeJaExisteException e) {
                conn.commit();
                throw e;
            }

            cozinheiro = cozinheiroDAO.salvar(conn, cozinheiro);
            conn.commit();
        } catch (SQLException e) {
            DatabaseConfig.rollback(conn);
            throw e;
        } finally {
            DatabaseConfig.close(conn);
        }

        return cozinheiro;
    }

    public void atualizar(Cozinheiro cozinheiro) throws EntidadeJaExisteException, SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            try {
                funcionarioValidator.validar(conn, cozinheiro);
            } catch (EntidadeJaExisteException e) {
                conn.commit();
                throw e;
            }

            cozinheiroDAO.atualizar(conn, cozinheiro);
            conn.commit();
        } catch (SQLException e) {
            DatabaseConfig.rollback(conn);
            throw e;
        } finally {
            DatabaseConfig.close(conn);
        }
    }

    public void deletar(Long id) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection()){

            cozinheiroDAO.deletar(conn, id);
        }

    }
}
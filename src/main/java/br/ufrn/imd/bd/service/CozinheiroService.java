package br.ufrn.imd.bd.service;

import br.ufrn.imd.bd.connection.DatabaseConfig;
import br.ufrn.imd.bd.dao.CozinheiroDAO;
import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.exceptions.EntidadeNaoExisteException;
import br.ufrn.imd.bd.model.Cozinheiro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
public class CozinheiroService {

    @Autowired
    private CozinheiroDAO cozinheiroDAO;

    public List<Cozinheiro> buscarTodos() throws SQLException {
        return cozinheiroDAO.buscarTodos();
    }

    public Cozinheiro buscarPorId(Long id) throws SQLException, EntidadeNaoExisteException {

        Cozinheiro cozinheiro = cozinheiroDAO.buscarPorChave(id);

        if(cozinheiro == null) {
            throw new EntidadeNaoExisteException("Cozinheiro não encontrado");
        }
        return cozinheiro;

    }

    public Cozinheiro salvar(Cozinheiro cozinheiro) throws SQLException, EntidadeJaExisteException {
        Connection conn = null;

        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            try {
                cozinheiro = cozinheiroDAO.salvar(conn, cozinheiro);
                conn.commit();
                return cozinheiro;
            } catch (SQLException e) {
                DatabaseConfig.rollback(conn);
                if (e.getErrorCode() == 1062) {
                    throw new EntidadeJaExisteException("Já existe um cozinheiro com esse login.");
                } else {
                    throw e;
                }
            }
        } finally {
            DatabaseConfig.close(conn);
        }
    }

    public void atualizar(Cozinheiro cozinheiro) throws EntidadeJaExisteException, SQLException, EntidadeNaoExisteException {

        buscarPorId(cozinheiro.getId());

        Connection conn = null;

        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            try {
                cozinheiroDAO.atualizar(conn, cozinheiro);
                conn.commit();
            } catch (SQLException e) {
                DatabaseConfig.rollback(conn);
                if (e.getErrorCode() == 1062) {
                    throw new EntidadeJaExisteException("Já existe um cozinheiro com esse login.");
                } else {
                    throw e;
                }
            }
        } finally {
            DatabaseConfig.close(conn);
        }
    }

    public void deletar(Long id) throws SQLException, EntidadeNaoExisteException {
        buscarPorId(id);
        try (Connection conn = DatabaseConfig.getConnection()){
            cozinheiroDAO.deletar(conn, id);
        }

    }
}
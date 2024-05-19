package br.ufrn.imd.bd.service;

import br.ufrn.imd.bd.connection.DatabaseConfig;
import br.ufrn.imd.bd.dao.CaixaDAO;
import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.model.Caixa;
import br.ufrn.imd.bd.model.Funcionario;
import br.ufrn.imd.bd.validation.FuncionarioValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
public class CaixaService {

    @Autowired
    private CaixaDAO caixaDAO;

    @Autowired
    private FuncionarioValidator funcionarioValidator;

    public List<Caixa> buscarTodos() throws SQLException {
        return caixaDAO.buscarTodos();
    }

    public Caixa buscarPorId(Long id) throws SQLException {
        return caixaDAO.buscarPorId(id);
    }

    public Funcionario salvar(Caixa caixa) throws SQLException, EntidadeJaExisteException {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            funcionarioValidator.validar(conn, caixa);
            caixa = caixaDAO.salvar(conn, caixa);
            conn.commit();
        } catch (SQLException e) {
            DatabaseConfig.rollback(conn);
            throw e;
        } finally {
            DatabaseConfig.close(conn);
        }

        return caixa;
    }

    public void atualizar(Caixa caixa) throws EntidadeJaExisteException, SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            funcionarioValidator.validar(conn, caixa);
            caixaDAO.atualizar(conn, caixa);
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
            caixaDAO.deletarPorId(conn, id);
            conn.commit();
        } catch (SQLException e) {
            DatabaseConfig.rollback(conn);
            throw e;
        } finally {
            DatabaseConfig.close(conn);
        }
    }
}

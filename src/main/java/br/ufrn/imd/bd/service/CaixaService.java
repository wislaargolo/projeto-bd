package br.ufrn.imd.bd.service;

import br.ufrn.imd.bd.connection.DatabaseConfig;
import br.ufrn.imd.bd.dao.CaixaDAO;
import br.ufrn.imd.bd.model.Caixa;
import br.ufrn.imd.bd.model.Funcionario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
public class CaixaService {

    @Autowired
    private CaixaDAO caixaDAO;

    public List<Caixa> buscarTodos() {
        return caixaDAO.buscarTodos();
    }

    public Funcionario buscarPorId(Long id) {
        return caixaDAO.buscarPorId(id);
    }

    public Funcionario salvar(Caixa caixa) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            caixa = caixaDAO.salvar(conn, caixa);
            conn.commit();
        } catch (SQLException e) {
            DatabaseConfig.rollback(conn);
            e.printStackTrace();
        } finally {
            DatabaseConfig.close(conn);
        }

        return caixa;
    }

    public void atualizar(Caixa caixa) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            caixaDAO.atualizar(conn, caixa);
            conn.commit();
        } catch (SQLException e) {
            DatabaseConfig.rollback(conn);
            e.printStackTrace();
        } finally {
            DatabaseConfig.close(conn);
        }
    }

    public void deletarPorId(Long id) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            caixaDAO.deletarPorId(conn, id);
            conn.commit();
        } catch (SQLException e) {
            DatabaseConfig.rollback(conn);
            e.printStackTrace();
        } finally {
            DatabaseConfig.close(conn);
        }
    }
}

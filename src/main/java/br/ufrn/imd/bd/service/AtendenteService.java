package br.ufrn.imd.bd.service;

import br.ufrn.imd.bd.connection.DatabaseConfig;
import br.ufrn.imd.bd.dao.AtendenteDAO;
import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.model.Atendente;
import br.ufrn.imd.bd.model.Funcionario;
import br.ufrn.imd.bd.model.enums.TipoAtendente;
import br.ufrn.imd.bd.validation.FuncionarioValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
public class AtendenteService {

    @Autowired
    private AtendenteDAO atendenteDAO;

    @Autowired
    private FuncionarioValidator funcionarioValidator;

    public List<Atendente> buscarPorTipo(TipoAtendente tipo) throws SQLException {
        return atendenteDAO.buscarPorTipo(tipo);
    }

    public Atendente buscarPorId(Long id) throws SQLException {
        return atendenteDAO.buscarPorChave(id);
    }

    public Funcionario salvar(Atendente atendente) throws SQLException, EntidadeJaExisteException {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            funcionarioValidator.validar(conn, atendente);
            atendente = atendenteDAO.salvar(conn, atendente);
            conn.commit();
        } catch (SQLException e) {
            DatabaseConfig.rollback(conn);
            throw e;
        } finally {
            DatabaseConfig.close(conn);
        }

        return atendente;
    }

    public void atualizar(Atendente atendente) throws EntidadeJaExisteException, SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            funcionarioValidator.validar(conn, atendente);
            atendenteDAO.atualizar(conn, atendente);
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
            atendenteDAO.deletar(conn, id);
        }

    }
}
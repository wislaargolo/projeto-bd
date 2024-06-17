package br.ufrn.imd.bd.service;

import br.ufrn.imd.bd.connection.DatabaseConfig;
import br.ufrn.imd.bd.dao.CaixaDAO;
import br.ufrn.imd.bd.dao.FuncionarioDAO;
import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.exceptions.EntidadeNaoExisteException;
import br.ufrn.imd.bd.model.Caixa;
import br.ufrn.imd.bd.model.Cozinheiro;
import br.ufrn.imd.bd.model.Funcionario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
public class CaixaService {

    @Autowired
    private CaixaDAO caixaDAO;

    @Autowired
    private FuncionarioDAO funcionarioDAO;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<Caixa> buscarTodos() throws SQLException {
        return caixaDAO.buscarTodos();
    }

    public Caixa buscarPorId(Long id) throws SQLException, EntidadeNaoExisteException {
        Caixa caixa = caixaDAO.buscarPorChave(id);
        if(caixa == null) {
            throw new EntidadeNaoExisteException("Caixa não encontrado");
        }
        return caixa;
    }

    public Caixa salvar(Caixa caixa) throws SQLException, EntidadeJaExisteException {
        Connection conn = null;

        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            String criptografada = passwordEncoder.encode(caixa.getSenha());
            caixa.setSenha(criptografada);

            try {
                caixa = caixaDAO.salvar(conn, caixa);
                conn.commit();
                return caixa;
            } catch (SQLException e) {
                DatabaseConfig.rollback(conn);
                if (e.getErrorCode() == 1062) {
                    reativarSeInativo(caixa);
                    throw new EntidadeJaExisteException("Um funcionário com esse login foi reativado.");
                } else {
                    throw e;
                }
            }
        } finally {
            DatabaseConfig.close(conn);
        }
    }

    public void atualizar(Caixa caixa) throws EntidadeJaExisteException, SQLException, EntidadeNaoExisteException {

        buscarPorId(caixa.getId());

        Connection conn = null;

        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            if(!caixa.getSenha().isEmpty()) {
                String criptografada = passwordEncoder.encode(caixa.getSenha());
                caixa.setSenha(criptografada);
            }

            try {
                caixaDAO.atualizar(conn, caixa);
                conn.commit();
            } catch (SQLException e) {
                DatabaseConfig.rollback(conn);
                if (e.getErrorCode() == 1062) {
                    reativarSeInativo(caixa);
                    throw new EntidadeJaExisteException("Um funcionário com esse login foi reativado.");
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
            caixaDAO.deletar(conn, id);
        }
    }

    private void reativarSeInativo(Caixa caixa) throws SQLException, EntidadeJaExisteException {
        try (Connection conn = DatabaseConfig.getConnection()) {
            Map<Funcionario, String> resultado = funcionarioDAO.buscarPorLogin(conn, caixa.getLogin());

            Map.Entry<Funcionario, String> entry = resultado.entrySet().iterator().next();
            Funcionario existente = entry.getKey();
            String papel = entry.getValue();

            if (existente.getAtivo()) {
                throw new EntidadeJaExisteException("Já existe um funcionário ativo com esse login!");
            } else if (!existente.getAtivo() && !papel.equals("CAIXA")) {
                throw new EntidadeJaExisteException("Existe um funcionário inativo com esse login em outro cargo! Não pode ser reativado.");
            }

            existente.setAtivo(true);
            caixaDAO.atualizar(conn, new Caixa(existente));
        }
    }
}




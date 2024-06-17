package br.ufrn.imd.bd.service;

import br.ufrn.imd.bd.connection.DatabaseConfig;
import br.ufrn.imd.bd.dao.CozinheiroDAO;
import br.ufrn.imd.bd.dao.FuncionarioDAO;
import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.exceptions.EntidadeNaoExisteException;
import br.ufrn.imd.bd.model.Cozinheiro;
import br.ufrn.imd.bd.model.Funcionario;
import br.ufrn.imd.bd.model.Mesa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
public class CozinheiroService {

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private CozinheiroDAO cozinheiroDAO;

    @Autowired
    private FuncionarioDAO funcionarioDAO;

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

            String criptografada = passwordEncoder.encode(cozinheiro.getSenha());
            cozinheiro.setSenha(criptografada);

            try {
                cozinheiro = cozinheiroDAO.salvar(conn, cozinheiro);
                conn.commit();
                return cozinheiro;
            } catch (SQLException e) {
                DatabaseConfig.rollback(conn);
                if (e.getErrorCode() == 1062) {
                    reativarSeInativo(cozinheiro);
                    throw new EntidadeJaExisteException("Um funcionário com esse login foi reativado.");
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

            if(!cozinheiro.getSenha().isEmpty()) {
                String criptografada = passwordEncoder.encode(cozinheiro.getSenha());
                cozinheiro.setSenha(criptografada);
            }

            try {
                cozinheiroDAO.atualizar(conn, cozinheiro);
                conn.commit();
            } catch (SQLException e) {
                DatabaseConfig.rollback(conn);
                if (e.getErrorCode() == 1062) {
                    reativarSeInativo(cozinheiro);
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
            cozinheiroDAO.deletar(conn, id);
        }

    }

    private void reativarSeInativo(Cozinheiro cozinheiro) throws SQLException, EntidadeJaExisteException {
        try (Connection conn = DatabaseConfig.getConnection()) {
            Map<Funcionario, String> resultado = funcionarioDAO.buscarPorLogin(conn, cozinheiro.getLogin());

            Map.Entry<Funcionario, String> entry = resultado.entrySet().iterator().next();
            Funcionario existente = entry.getKey();
            String papel = entry.getValue();

            if (existente.getAtivo()) {
                throw new EntidadeJaExisteException("Já existe um funcionário ativo com esse login!");
            } else if (!existente.getAtivo() && !papel.equals("COZINHEIRO")) {
                throw new EntidadeJaExisteException("Existe um funcionário inativo com esse login em outro cargo! Não pode ser reativado.");
            }

            existente.setAtivo(true);
            cozinheiroDAO.atualizar(conn, new Cozinheiro(existente));
        }
    }
}
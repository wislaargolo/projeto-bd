package br.ufrn.imd.bd.service;

import br.ufrn.imd.bd.connection.DatabaseUtil;
import br.ufrn.imd.bd.dao.AtendenteDAO;
import br.ufrn.imd.bd.dao.FuncionarioDAO;
import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.exceptions.EntidadeNaoExisteException;
import br.ufrn.imd.bd.model.Atendente;
import br.ufrn.imd.bd.model.Caixa;
import br.ufrn.imd.bd.model.Funcionario;
import br.ufrn.imd.bd.model.enums.TipoAtendente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
public class AtendenteService {

    @Autowired
    private AtendenteDAO atendenteDAO;
    @Autowired
    private FuncionarioDAO funcionarioDAO;

    @Autowired
    private DatabaseUtil databaseUtil;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<Atendente> buscarPorTipo(TipoAtendente tipo) throws SQLException {
        return atendenteDAO.buscarPorTipo(tipo);
    }
    public Atendente buscarPorId(Long id) throws SQLException, EntidadeNaoExisteException {
        Atendente atendente = atendenteDAO.buscarPorChave(id);
        if(atendente == null) {
            throw new EntidadeNaoExisteException("Atendente não encontrado.");
        }
        return atendente;
    }

    public Atendente salvar(Atendente atendente) throws SQLException, EntidadeJaExisteException {
        Connection conn = null;

        try {
            conn = databaseUtil.getConnection();
            conn.setAutoCommit(false);

            String criptografada = passwordEncoder.encode(atendente.getSenha());
            atendente.setSenha(criptografada);

            try {
                Atendente novo = atendenteDAO.salvar(conn, atendente);
                conn.commit();
                return novo;
            } catch (SQLException e) {
                databaseUtil.rollback(conn);
                if (e.getErrorCode() == 1062) {
                    reativarSeInativo(atendente);
                    throw new EntidadeJaExisteException("Um funcionário com esse login foi reativado.");
                } else {
                    throw e;
                }
            }
        } finally {
            databaseUtil.close(conn);
        }
    }

    public void atualizar(Atendente atendente) throws EntidadeJaExisteException, SQLException, EntidadeNaoExisteException {

        buscarPorId(atendente.getId());

        Connection conn = null;

        try {
            conn = databaseUtil.getConnection();
            conn.setAutoCommit(false);

            if(!atendente.getSenha().isEmpty()) {
                String encriptada = passwordEncoder.encode(atendente.getSenha());
                atendente.setSenha(encriptada);
            }

            try {
                atendenteDAO.atualizar(conn, atendente);
                conn.commit();
            } catch (SQLException e) {
                databaseUtil.rollback(conn);
                if (e.getErrorCode() == 1062) {
                    reativarSeInativo(atendente);
                    throw new EntidadeJaExisteException("Um funcionário com esse login foi reativado.");
                } else {
                    throw e;
                }
            }
        } finally {
            databaseUtil.close(conn);
        }
    }

    public void deletar(Long id) throws SQLException, EntidadeNaoExisteException {
        buscarPorId(id);
        try (Connection conn = databaseUtil.getConnection()){
            atendenteDAO.deletar(conn, id);
        }
    }

    private void reativarSeInativo(Atendente atendente) throws SQLException, EntidadeJaExisteException {
        try (Connection conn = databaseUtil.getConnection()) {
            Map<Funcionario, String> resultado = funcionarioDAO.buscarPorLogin(conn, atendente.getLogin());

            Map.Entry<Funcionario, String> entry = resultado.entrySet().iterator().next();
            Funcionario existente = entry.getKey();
            String papel = entry.getValue();

            if (existente.getAtivo()) {
                throw new EntidadeJaExisteException("Já existe um funcionário ativo com esse login!");
            } else if (!existente.getAtivo() && !papel.equals(atendente.getTipo().name())) {
                throw new EntidadeJaExisteException("Existe um funcionário inativo com esse login em outro cargo! Não pode ser reativado.");
            }

            existente.setAtivo(true);
            atendenteDAO.atualizar(conn, new Atendente(existente));
        }
    }
}
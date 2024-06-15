package br.ufrn.imd.bd.service;

import br.ufrn.imd.bd.connection.DatabaseConfig;
import br.ufrn.imd.bd.dao.FuncionarioDAO;
import br.ufrn.imd.bd.dao.TelefoneDAO;
import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.exceptions.EntidadeNaoExisteException;
import br.ufrn.imd.bd.model.Telefone;
import br.ufrn.imd.bd.model.TelefoneKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
public class TelefoneService {

    @Autowired
    private TelefoneDAO telefoneDAO;

    @Autowired
    private FuncionarioDAO funcionarioDAO;

    public List<Telefone> buscarTelefonesPorFuncionarioId(Long id) throws SQLException {
        List<Telefone> telefones = telefoneDAO.buscarPorFuncionarioId(id);
        return telefones;
    }

    public Telefone buscarPorChave(TelefoneKey telefoneKey) throws SQLException, EntidadeNaoExisteException {
        Telefone telefone = telefoneDAO.buscarPorChave(telefoneKey);

        if(telefone == null) {
            throw new EntidadeNaoExisteException("Telefone não encontrado.");
        }

        return telefone;
    }

    public Telefone salvar(Telefone telefone) throws SQLException, EntidadeJaExisteException, EntidadeNaoExisteException {
        try (Connection conn = DatabaseConfig.getConnection()){
            telefone = telefoneDAO.salvar(conn, telefone);
            return telefone;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new EntidadeJaExisteException("Este telefone já existe para o funcionário!");
            } else if (e.getErrorCode() == 1452) {
                throw new EntidadeNaoExisteException("Funcionário associado ao telefone não existe");
            } else {
                throw e;
            }
        }
    }

    public void atualizar(Telefone telefoneAntigo, Telefone telefoneNovo) throws SQLException, EntidadeJaExisteException, EntidadeNaoExisteException {
        try (Connection conn = DatabaseConfig.getConnection()){
            telefoneDAO.atualizar(conn, telefoneAntigo, telefoneNovo);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new EntidadeJaExisteException("Este telefone já existe para o funcionário!");
            } else if (e.getErrorCode() == 1452) {
                throw new EntidadeNaoExisteException("Funcionário associado ao telefone não existe");
            } else {
                throw e;
            }
        }
    }

    public void deletar(String telefone, Long funcionarioId) throws SQLException, EntidadeNaoExisteException {
        TelefoneKey telefoneKey = new TelefoneKey(funcionarioId, telefone);
        buscarPorChave(telefoneKey);
        try (Connection conn = DatabaseConfig.getConnection()) {
            telefoneDAO.deletar(conn, new TelefoneKey(funcionarioId, telefone));
        }
    }
}
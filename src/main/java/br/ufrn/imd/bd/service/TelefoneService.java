package br.ufrn.imd.bd.service;

import br.ufrn.imd.bd.connection.DatabaseConfig;
import br.ufrn.imd.bd.dao.FuncionarioDAO;
import br.ufrn.imd.bd.dao.TelefoneDAO;
import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.model.Funcionario;
import br.ufrn.imd.bd.model.Telefone;
import br.ufrn.imd.bd.model.TelefoneKey;
import br.ufrn.imd.bd.validation.TelefoneValidator;
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

    @Autowired
    private TelefoneValidator telefoneValidator;

    public List<Telefone> buscarTelefonesPorFuncionarioId(Long id) throws SQLException {
        List<Telefone> telefones = telefoneDAO.buscarPorFuncionarioId(id);
        return telefones;
    }

    public Telefone salvar(Telefone telefone) throws SQLException, EntidadeJaExisteException {
        try (Connection conn = DatabaseConfig.getConnection()){
            telefoneValidator.validar(conn, telefone.getKey());
            telefone = telefoneDAO.salvar(conn, telefone);
        }

        return telefone;
    }

    public void atualizar(Telefone telefoneAntigo, Telefone telefoneNovo) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection()){
            telefoneDAO.atualizar(conn, telefoneAntigo, telefoneNovo);
        }
    }

    public void deletar(String telefone, Long funcionarioId) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection()){
            telefoneDAO.deletarPorId(conn, new TelefoneKey(funcionarioId, telefone));
        }
    }
}
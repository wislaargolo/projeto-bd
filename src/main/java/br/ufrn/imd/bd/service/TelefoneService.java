package br.ufrn.imd.bd.service;

import br.ufrn.imd.bd.connection.DatabaseConfig;
import br.ufrn.imd.bd.controller.dto.TelefoneDTO;
import br.ufrn.imd.bd.dao.FuncionarioDAO;
import br.ufrn.imd.bd.dao.TelefoneDAO;
import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.model.Funcionario;
import br.ufrn.imd.bd.model.Telefone;
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
    private TelefoneValidator telefoneValidator;

    @Autowired
    private FuncionarioDAO funcionarioDAO;

    public List<Telefone> buscarTodos() throws SQLException {
        List<Telefone> telefoneList = telefoneDAO.buscarTodos();

        for(Telefone telefone: telefoneList) {
            Long id_funcionario = telefone.getFuncionario().getId();
            telefone.setFuncionario(funcionarioDAO.buscarPorId(id_funcionario));
        }

        return  telefoneList;
    }
    public Telefone salvar(Telefone telefone) throws SQLException, EntidadeJaExisteException {
        try (Connection conn = DatabaseConfig.getConnection()){
            telefoneValidator.validar(conn, telefone);
            telefone = telefoneDAO.salvar(conn, telefone);
        }

        return telefone;
    }

    public void deletar(String telefone, Long funcionarioId) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection()){
            telefoneDAO.deletar(conn, telefone, funcionarioId);
        }
    }

    public void atualizar(Telefone telefoneAntigo, Telefone telefoneNovo) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection()){
            telefoneDAO.atualizar(conn, telefoneAntigo, telefoneNovo);
        }
    }

    public TelefoneDTO getTelefoneDTO(String telefone, Long funcionarioId) {
        Funcionario funcionario = new Funcionario();
        funcionario.setId(funcionarioId);
        Telefone telefoneObj = new Telefone(telefone, funcionario);
        TelefoneDTO telefoneDTO = new TelefoneDTO();
        telefoneDTO.setTelefoneHidden(telefoneObj);
        telefoneDTO.setTelefoneNovo(telefoneObj);
        return telefoneDTO;
    }
}

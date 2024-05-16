package br.ufrn.imd.bd.service;

import br.ufrn.imd.bd.dao.FuncionarioDAO;
import br.ufrn.imd.bd.model.Funcionario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class FuncionarioService {

    @Autowired
    private FuncionarioDAO funcionarioDAO;

    public List<Funcionario> buscarTodos() throws SQLException {
        return funcionarioDAO.buscarTodos();
    }
}

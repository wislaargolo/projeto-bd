package br.ufrn.imd.bd.service;

import br.ufrn.imd.bd.connection.DatabaseUtil;
import br.ufrn.imd.bd.dao.FuncionarioDAO;
import br.ufrn.imd.bd.exceptions.EntidadeNaoExisteException;
import br.ufrn.imd.bd.model.Funcionario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
public class FuncionarioService implements UserDetailsService  {

    @Autowired
    private FuncionarioDAO funcionarioDAO;

    @Autowired
    private DatabaseUtil databaseUtil;

    public List<Funcionario> buscarTodos() throws SQLException {
        return funcionarioDAO.buscarTodos();
    }

    public Funcionario buscarPorId(Long id) throws SQLException, EntidadeNaoExisteException {
        Funcionario funcionario = funcionarioDAO.buscarPorChave(id);

        if(funcionario == null) {
            throw new EntidadeNaoExisteException("Funcionário não encontrado");
        }

        return funcionario;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return funcionarioDAO.carregarPorLogin(username);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

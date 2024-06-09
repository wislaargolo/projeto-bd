package br.ufrn.imd.bd.validation;

import br.ufrn.imd.bd.dao.FuncionarioDAO;
import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.exceptions.SenhaInvalidaException;
import br.ufrn.imd.bd.model.Funcionario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;

@Component
public class FuncionarioValidator {

    @Autowired
    private FuncionarioDAO funcionarioDAO;

    public void validar(Connection conn, Funcionario funcionario) throws SQLException, EntidadeJaExisteException {

        if(funcionarioDAO.existeFuncionarioComParametroEId(conn, "email", funcionario.getEmail(), funcionario.getId())) {
            throw new EntidadeJaExisteException("Já existe um funcionário com esse email!");
        }

        if(funcionarioDAO.existeFuncionarioComParametroEId(conn, "login", funcionario.getLogin(), funcionario.getId())) {
            throw new EntidadeJaExisteException("Já existe um funcionário com esse login!");
        }

    }

    public void validarSenha(Funcionario funcionario, String confirmacaoSenha, boolean isNew) throws SenhaInvalidaException  {

        if (!funcionario.getSenha().equals(confirmacaoSenha)) {
            throw new SenhaInvalidaException("As senhas não coincidem!");
        }

    }




}

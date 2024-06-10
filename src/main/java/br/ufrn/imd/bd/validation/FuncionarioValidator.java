package br.ufrn.imd.bd.validation;

import br.ufrn.imd.bd.dao.FuncionarioDAO;
import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
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

        Funcionario existente = funcionarioDAO.buscarPorParametros(conn, funcionario.getEmail(), funcionario.getLogin());

        if (existente != null && !existente.getId().equals(funcionario.getId())) {
            if (existente.getAtivo()) {
                if (existente.getEmail().equals(funcionario.getEmail())) {
                    throw new EntidadeJaExisteException("Já existe um funcionário ativo com esse email!");
                }
                if (existente.getLogin().equals(funcionario.getLogin())) {
                    throw new EntidadeJaExisteException("Já existe um funcionário ativo com esse login!");
                }
            } else {
                existente.setAtivo(true);
                funcionarioDAO.atualizar(conn, existente);
                throw new EntidadeJaExisteException("Um funcionário inativo com esse email ou login foi reativado. Por favor, edite o funcionário reativado.");
            }
        }

    }



}

package br.ufrn.imd.bd.validation;

import br.ufrn.imd.bd.dao.FuncionarioDAO;
import br.ufrn.imd.bd.dao.MesaDAO;
import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.model.Funcionario;
import br.ufrn.imd.bd.model.Mesa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;

@Component
public class MesaValidator {

    @Autowired
    private MesaDAO mesaDAO;

    public void validar(Connection conn, Mesa mesa) throws SQLException, EntidadeJaExisteException {

        Mesa existente = mesaDAO.buscarPorIdentificacao(conn, mesa.getIdentificacao());

        if (existente != null && existente.getId().equals(mesa.getId())) {
            return;
        }

        if (existente != null && existente.getAtivo()) {
            throw new EntidadeJaExisteException("Já existe uma mesa ativa com essa identificação!");
        } else if (existente != null && !existente.getAtivo()) {
            existente.setAtivo(true);
            mesaDAO.atualizar(conn, existente);
            throw new EntidadeJaExisteException("Uma mesa inativa com essa identificação foi reativada. Por favor, edite a mesa reativada.");
        }
    }
}



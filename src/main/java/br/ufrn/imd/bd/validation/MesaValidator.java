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

        if(mesaDAO.existeMesaComIdentificacao(conn, "identificacao", mesa.getIdentificacao(), mesa.getId())) {
            throw new EntidadeJaExisteException("Já existe uma mesa com essa identificação!");
        }

    }

}

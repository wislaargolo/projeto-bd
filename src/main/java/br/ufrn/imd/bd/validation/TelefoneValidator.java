package br.ufrn.imd.bd.validation;

import br.ufrn.imd.bd.dao.TelefoneDAO;
import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.model.Telefone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;

@Component
public class TelefoneValidator {

    @Autowired
    private TelefoneDAO telefoneDAO;

    public void validar(Connection conn, Telefone telefone) throws SQLException, EntidadeJaExisteException {

        if(telefoneDAO.existeTelefone(conn, telefone)) {
            throw new EntidadeJaExisteException("Este telefone já existe para o funcionário!");
        }
    }
}
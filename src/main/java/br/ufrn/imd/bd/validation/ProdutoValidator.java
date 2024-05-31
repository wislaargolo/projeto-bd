package br.ufrn.imd.bd.validation;

import br.ufrn.imd.bd.dao.InstanciaProdutoDAO;
import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.model.InstanciaProduto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;

@Component
public class ProdutoValidator {

    @Autowired
    private InstanciaProdutoDAO instanciaProdutoDAO;

    public void validar(Connection conn, InstanciaProduto instanciaProduto) throws EntidadeJaExisteException, SQLException {
        if(instanciaProdutoDAO.existeProdutoNome(conn, instanciaProduto)) {
            throw new EntidadeJaExisteException("Já existe um produto ativo com esse nome.");
        }
    }
}

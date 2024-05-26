package br.ufrn.imd.bd.validation;

import br.ufrn.imd.bd.dao.ProdutoDAO;
import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.model.Produto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;

@Component
public class ProdutoValidator {

    @Autowired
    private ProdutoDAO produtoDAO;

    public void validar(Connection conn, Produto produto) throws EntidadeJaExisteException, SQLException {
        if(produtoDAO.existeProduto(conn, produto)) {
            throw new EntidadeJaExisteException("Já existe um produto com esse nome!");
        }
    }
}

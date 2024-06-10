package br.ufrn.imd.bd.validation;

import br.ufrn.imd.bd.dao.InstanciaProdutoDAO;
import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.model.Funcionario;
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
        InstanciaProduto existente = instanciaProdutoDAO.buscarUltimaInstanciaPorDescricao(conn, instanciaProduto.getProduto().getDescricao());

        if (existente != null && existente.getId().equals(instanciaProduto.getId())) {
            return;
        }

        if (existente != null && existente.getAtivo()) {
            throw new EntidadeJaExisteException("Já existe um produto ativo com essa descrição!");
        } else if (existente != null && !existente.getAtivo()) {
            existente.setAtivo(true);
            instanciaProdutoDAO.atualizar(conn, existente);
            throw new EntidadeJaExisteException("Um produto inativo com essa identificação foi reativado. Por favor, edite o produto reativado.");
        }
    }
}

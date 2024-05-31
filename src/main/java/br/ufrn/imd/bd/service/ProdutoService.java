package br.ufrn.imd.bd.service;

import br.ufrn.imd.bd.connection.DatabaseConfig;
import br.ufrn.imd.bd.dao.InstanciaProdutoDAO;
import br.ufrn.imd.bd.dao.ProdutoDAO;
import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.model.InstanciaProduto;
import br.ufrn.imd.bd.model.Produto;
import br.ufrn.imd.bd.validation.ProdutoValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private InstanciaProdutoDAO instanciaProdutoDAO;

    @Autowired
    private ProdutoDAO produtoDAO;

    @Autowired
    private ProdutoValidator produtoValidator;

    public List<InstanciaProduto> buscarTodos() throws SQLException {
        return instanciaProdutoDAO.buscarTodos();
    }

    public Object buscarPorId(Long id) throws SQLException {
        return instanciaProdutoDAO.buscarPorId(id);
    }

    public void deletar(Long id) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection()){
            InstanciaProduto instanciaProduto = instanciaProdutoDAO.buscarPorId(id);
            instanciaProduto.setAtivo(false);
            instanciaProdutoDAO.atualizar(conn, instanciaProduto);
        }
    }

    public InstanciaProduto salvar(InstanciaProduto instanciaProduto) throws SQLException, EntidadeJaExisteException {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            produtoValidator.validar(conn, instanciaProduto);
            instanciaProduto.setProduto(this.salvarProduto(conn, instanciaProduto.getProduto()));
            instanciaProduto = this.salvarInstanciaProduto(conn, instanciaProduto);
            conn.commit();
        } catch (SQLException e) {
            DatabaseConfig.rollback(conn);
            throw e;
        } finally {
            DatabaseConfig.close(conn);
        }

        return instanciaProduto;
    }

    private InstanciaProduto salvarInstanciaProduto(Connection conn, InstanciaProduto instanciaProduto) throws SQLException {
        if (instanciaProduto.getId() == null) {
            return instanciaProdutoDAO.salvar(conn, instanciaProduto);
        }

        InstanciaProduto antiga = instanciaProdutoDAO.buscarPorId(instanciaProduto.getId());

        if (antiga.getValor().compareTo(instanciaProduto.getValor()) != 0) {
            instanciaProdutoDAO.salvar(conn, instanciaProduto);
            antiga.setAtivo(false);
            instanciaProdutoDAO.atualizar(conn, antiga);
        } else {
            instanciaProdutoDAO.atualizar(conn, instanciaProduto);
        }

        return instanciaProduto;
    }


    private Produto salvarProduto(Connection conn, Produto produto) throws SQLException, EntidadeJaExisteException {
        if (produto.getId() == null) {
            produto = produtoDAO.salvar(conn, produto);
        } else {
            produtoDAO.atualizar(conn, produto);
        }

        return produto;
    }
}
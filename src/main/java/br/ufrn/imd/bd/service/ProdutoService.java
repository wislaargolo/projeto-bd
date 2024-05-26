package br.ufrn.imd.bd.service;

import br.ufrn.imd.bd.connection.DatabaseConfig;
import br.ufrn.imd.bd.controller.dto.ProdutoDTO;
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
import java.util.ArrayList;
import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private InstanciaProdutoDAO instanciaProdutoDAO;

    @Autowired
    private ProdutoDAO produtoDAO;

    @Autowired
    private ProdutoValidator produtoValidator;

    public List<ProdutoDTO> buscarTodos() throws SQLException {
        List<ProdutoDTO> produtoDTOList = new ArrayList<>();
        List<Produto> produtos = produtoDAO.buscarTodos();

        for(Produto produto: produtos) {
            InstanciaProduto instanciaProduto = instanciaProdutoDAO.buscarPorProdutoId(produto.getId());
            produtoDTOList.add(getProdutoDTO(instanciaProduto, produto));
        }

        return produtoDTOList;
    }

    public ProdutoDTO buscarPorId(Long id) throws SQLException {
        Produto produto = produtoDAO.buscarPorId(id);
        InstanciaProduto instanciaProduto = instanciaProdutoDAO.buscarPorProdutoId(produto.getId());

        return getProdutoDTO(instanciaProduto, produto);
    }

    public ProdutoDTO salvar(InstanciaProduto instanciaProduto, Produto produto) throws SQLException, EntidadeJaExisteException {
        Connection conn = null;
        ProdutoDTO produtoDTO;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            produto = this.salvarProduto(conn, produto);
            instanciaProduto.setProdutoId(produto.getId());
            instanciaProduto = this.salvarInstanciaProduto(conn, instanciaProduto);
            produtoDTO = getProdutoDTO(instanciaProduto, produto);
            conn.commit();
        } catch (SQLException e) {
            DatabaseConfig.rollback(conn);
            throw e;
        } finally {
            DatabaseConfig.close(conn);
        }

        return produtoDTO;
    }

    private InstanciaProduto salvarInstanciaProduto(Connection conn, InstanciaProduto instanciaProduto) throws SQLException {
        InstanciaProduto antiga = instanciaProdutoDAO.buscarPorProdutoId(instanciaProduto.getProdutoId());
        if(antiga == null || antiga.getValor().compareTo(instanciaProduto.getValor()) != 0) {
            instanciaProduto = instanciaProdutoDAO.salvar(conn, instanciaProduto);
        } else {
            instanciaProdutoDAO.atualizar(conn, instanciaProduto);
        }

        return instanciaProduto;
    }

    private Produto salvarProduto(Connection conn, Produto produto) throws SQLException, EntidadeJaExisteException {
        produtoValidator.validar(conn, produto);
        produto.setAtivo(true);

        if(produto.getId() == null) {
            produto = produtoDAO.salvar(conn, produto);
        } else {
            produtoDAO.atualizar(conn, produto);
        }

        return produto;
    }

    public void atualizar(InstanciaProduto instanciaProduto) throws EntidadeJaExisteException, SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            instanciaProdutoDAO.atualizar(conn, instanciaProduto);
            conn.commit();
        } catch (SQLException e) {
            DatabaseConfig.rollback(conn);
            throw e;
        } finally {
            DatabaseConfig.close(conn);
        }
    }

    public void deletarPorId(Long id) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            instanciaProdutoDAO.deletarPorId(conn, id);
            conn.commit();
        } catch (SQLException e) {
            DatabaseConfig.rollback(conn);
            throw e;
        } finally {
            DatabaseConfig.close(conn);
        }
    }

    public void inativarProduto(Long id) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            Produto produto = produtoDAO.buscarPorId(id);
            produto.setAtivo(false);
            produtoDAO.atualizar(conn, produto);
            conn.commit();
        } catch (SQLException e) {
            DatabaseConfig.rollback(conn);
            throw e;
        } finally {
            DatabaseConfig.close(conn);
        }
    }

    public ProdutoDTO getProdutoDTO(InstanciaProduto instanciaProduto, Produto produto) {
        return new ProdutoDTO(instanciaProduto, produto);
    }
}

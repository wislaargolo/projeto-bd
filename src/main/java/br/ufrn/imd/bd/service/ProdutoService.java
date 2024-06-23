package br.ufrn.imd.bd.service;

import br.ufrn.imd.bd.connection.DatabaseUtil;
import br.ufrn.imd.bd.dao.InstanciaProdutoDAO;
import br.ufrn.imd.bd.dao.ProdutoDAO;
import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.exceptions.EntidadeNaoExisteException;
import br.ufrn.imd.bd.model.InstanciaProduto;
import br.ufrn.imd.bd.model.Produto;
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
    private DatabaseUtil databaseUtil;

    public List<InstanciaProduto> buscarTodos() throws SQLException {
        return instanciaProdutoDAO.buscarTodos();
    }

    public List<InstanciaProduto> buscarTodosDisponiveis() throws SQLException {
        return instanciaProdutoDAO.buscarProdutosDisponivei();
    }

    public InstanciaProduto buscarPorId(Long id) throws SQLException, EntidadeNaoExisteException {

        InstanciaProduto instanciaProduto = instanciaProdutoDAO.buscarPorChave(id);
        if (instanciaProduto == null) {
            throw new EntidadeNaoExisteException("Produto não encontrado.");
        }
        return instanciaProduto;
    }

    public void deletar(Long id) throws SQLException, EntidadeNaoExisteException {

        buscarPorId(id);

        try (Connection conn = databaseUtil.getConnection()) {
            instanciaProdutoDAO.deletar(conn, id);
        }
    }

    public InstanciaProduto salvar(InstanciaProduto instanciaProduto) throws SQLException, EntidadeJaExisteException, EntidadeNaoExisteException {
        Connection conn = null;
        try {
            conn = databaseUtil.getConnection();
            conn.setAutoCommit(false);

            try {
                instanciaProduto.setProduto(this.salvarProduto(conn, instanciaProduto.getProduto()));
                instanciaProduto = this.salvarInstanciaProduto(conn, instanciaProduto);
                conn.commit();
                return instanciaProduto;
            } catch (SQLException e) {
                databaseUtil.rollback(conn);
                if (e.getErrorCode() == 1062) {
                    reativarSeInativo(instanciaProduto);
                    throw new EntidadeJaExisteException("Um produto inativo com essa descrição foi reativado.");
                } else {
                    throw e;
                }
            }
        } finally {
            databaseUtil.close(conn);
        }
    }


    private InstanciaProduto salvarInstanciaProduto(Connection conn, InstanciaProduto instanciaProduto) throws SQLException, EntidadeNaoExisteException {
        if (instanciaProduto.getId() == null) {
            return instanciaProdutoDAO.salvar(conn, instanciaProduto);
        }

        InstanciaProduto antiga = instanciaProdutoDAO.buscarPorChave(instanciaProduto.getId());

        if (antiga.getValor().compareTo(instanciaProduto.getValor()) != 0) {
            instanciaProdutoDAO.deletar(conn, antiga.getId());
            instanciaProdutoDAO.salvar(conn, instanciaProduto);
        } else {
            this.buscarPorId(instanciaProduto.getId());
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

    private void reativarSeInativo(InstanciaProduto instanciaProduto) throws SQLException, EntidadeJaExisteException {
        try (Connection conn = databaseUtil.getConnection()) {
            InstanciaProduto existente = instanciaProdutoDAO.buscarUltimaInstanciaPorDescricao(conn, instanciaProduto.getProduto().getDescricao());

            if (existente != null && existente.getAtivo()) {
                throw new EntidadeJaExisteException("Já existe um produto ativo com essa descrição!");
            } else if (existente != null && !existente.getAtivo()) {
                existente.setAtivo(true);
                instanciaProdutoDAO.atualizar(conn, existente);
            }
        }
    }

}
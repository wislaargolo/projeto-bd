package br.ufrn.imd.bd.dao;

import br.ufrn.imd.bd.model.Produto;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProdutoDAO extends AbstractDAOImpl<Produto, Long>{

    @Override
    protected Produto mapearResultado(ResultSet rs) throws SQLException {
        Produto produto = new Produto();
        produto.setId(rs.getLong("id"));
        produto.setNome(rs.getString("nome"));
        produto.setAtivo(rs.getBoolean("is_ativo"));
        return produto;
    }

    @Override
    public String getNomeTabela() {
        return "produtos";
    }

    @Override
    public List<Produto> buscarTodos() throws SQLException {
        List<Produto> resultados = new ArrayList<>();
        String sql = String.format("SELECT * FROM %s WHERE is_ativo = ?", getNomeTabela());
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, true);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    resultados.add(mapearResultado(rs));
                }
            }
        }
        return resultados;
    }

    @Override
    public Produto salvar(Connection conn, Produto produto) throws SQLException {
        String sql = String.format("INSERT INTO %s (nome, is_ativo) VALUES (?,?)", getNomeTabela());

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, produto.getNome());
            stmt.setBoolean(2, produto.getAtivo());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("ERRO >> A inserção de mesa falhou, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    produto.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("ERRO >> A inserção de mesa falhou, nenhum ID gerado.");
                }
            }
        }

        return produto;
    }

    @Override
    public void atualizar(Connection conn, Produto... produtos) throws SQLException {
        if (produtos.length != 1) {
            throw new IllegalArgumentException("ERRO >> Apenas um produto para atualização.");
        }

        Produto novo = produtos[0];

        String sql = String.format(
                "UPDATE %s SET nome = ?, is_ativo = ? WHERE id = ?",
                getNomeTabela()
        );

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, novo.getNome());
            stmt.setBoolean(2, novo.getAtivo());
            stmt.setLong(3, novo.getId());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("ERRO >> Atualização falhou.");
            }
        }
    }

    public boolean existeProduto(Connection conn, Produto produto) throws SQLException {
        String sql = String.format("SELECT * FROM %s WHERE nome = ? AND is_ativo = ? AND id != ?", getNomeTabela());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, produto.getNome());
            stmt.setBoolean(2, true);
            stmt.setObject(3, produto.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return true;
            }
        }
        return false;
    }
}

package br.ufrn.imd.bd.dao;

import br.ufrn.imd.bd.model.Produto;
import org.springframework.stereotype.Component;

import java.sql.*;

@Component
public class ProdutoDAO extends AbstractDAO<Produto, Long> {

    @Override
    public Produto mapearResultado(ResultSet rs) throws SQLException {
        Produto produto = new Produto();
        produto.setId(rs.getLong("id_produto"));
        produto.setNome(rs.getString("nome"));
        produto.setDisponivel(rs.getBoolean("is_disponivel"));
        return produto;
    }

    @Override
    public String getNomeTabela() {
        return "produtos";
    }

    @Override
    public Produto salvar(Connection conn, Produto produto) throws SQLException {
        String sql = String.format("INSERT INTO %s (nome) VALUES (?)", getNomeTabela());

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, produto.getNome());

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
                "UPDATE %s SET nome = ?, is_disponivel = ? WHERE id_produto = ?",
                getNomeTabela()
        );

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, novo.getNome());
            stmt.setBoolean(2, novo.getDisponivel());
            stmt.setLong(3, novo.getId());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("ERRO >> Atualização falhou.");
            }
        }
    }
}

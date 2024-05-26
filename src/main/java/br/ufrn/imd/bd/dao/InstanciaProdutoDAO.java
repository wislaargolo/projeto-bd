package br.ufrn.imd.bd.dao;

import br.ufrn.imd.bd.model.*;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.LocalDateTime;

@Component
public class InstanciaProdutoDAO extends AbstractDAOImpl<InstanciaProduto, Long> {
    @Override
    protected InstanciaProduto mapearResultado(ResultSet rs) throws SQLException {
        InstanciaProduto instanciaProduto = new InstanciaProduto();
        instanciaProduto.setId(rs.getLong("id"));
        instanciaProduto.setValor(rs.getDouble("valor"));
        instanciaProduto.setDisponivel(rs.getBoolean("is_disponivel"));
        instanciaProduto.setData(rs.getObject("data", LocalDateTime.class));
        instanciaProduto.setProdutoId(rs.getLong("id_produto"));
        return instanciaProduto;
    }

    @Override
    public String getNomeTabela() {
        return "produto_instancias";
    }

    public InstanciaProduto buscarPorProdutoId(Long id) throws SQLException {
        String sql = String.format("SELECT * FROM %s WHERE id_produto = ? ORDER BY data desc LIMIT 1", getNomeTabela());
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapearResultado(rs);
            }
        }
        return null;
    }

    @Override
    public InstanciaProduto salvar(Connection conn, InstanciaProduto instanciaProduto) throws SQLException {
        String sql = String.format("INSERT INTO %s (valor, is_disponivel, id_produto) VALUES (?, ?, ?)", getNomeTabela());

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDouble(1, instanciaProduto.getValor());
            stmt.setBoolean(2, instanciaProduto.getDisponivel());
            stmt.setLong(3, instanciaProduto.getProdutoId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("ERRO >> A inserção de instancia produto falhou, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    instanciaProduto.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("ERRO >> A inserção do instancia produto falhou, nenhum ID gerado.");
                }
            }
        }

        return instanciaProduto;
    }

    @Override
    public void atualizar(Connection conn, InstanciaProduto... instanciaProdutos) throws SQLException {
        if (instanciaProdutos.length != 1) {
            throw new IllegalArgumentException("ERRO >> Apenas um funcionário para atualização.");
        }

        InstanciaProduto novo = instanciaProdutos[0];

        String sql = String.format(
                "UPDATE %s SET valor = ?, is_disponivel = ?, id_produto = ? WHERE id = ?",
                getNomeTabela()
        );

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, novo.getValor());
            stmt.setBoolean(2, novo.getDisponivel());
            stmt.setLong(3, novo.getProdutoId());
            stmt.setLong(4, novo.getId());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("ERRO >> Atualização falhou.");
            }
        }
    }
}

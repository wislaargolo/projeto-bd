package br.ufrn.imd.bd.dao;

import br.ufrn.imd.bd.model.Mesa;
import org.springframework.stereotype.Component;

import java.sql.*;

@Component
public class MesaDAO extends AbstractDAO<Mesa, Long> {

    @Override
    public Mesa mapearResultado(ResultSet rs) throws SQLException {
        Mesa mesa = new Mesa();
        mesa.setId(rs.getLong("id"));
        mesa.setDescricao(rs.getString("descricao"));
        return mesa;
    }

    @Override
    public String getNomeTabela() {
        return "mesas";
    }

    @Override
    public Mesa salvar(Connection conn, Mesa mesa) throws SQLException {
        String sql = String.format("INSERT INTO %s (descricao) VALUES (?)", getNomeTabela());

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, mesa.getDescricao());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("ERRO >> A inserção de mesa falhou, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    mesa.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("ERRO >> A inserção de mesa falhou, nenhum ID gerado.");
                }
            }
        }

        return mesa;
    }

    @Override
    public void atualizar(Connection conn, Mesa... mesas) throws SQLException {
        if (mesas.length != 1) {
            throw new IllegalArgumentException("ERRO >> Apenas uma mesa para atualização.");
        }

        Mesa novo = mesas[0];

        String sql = String.format(
                "UPDATE %s SET descricao = ? WHERE id = ?",
                getNomeTabela()
        );

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, novo.getDescricao());
            stmt.setLong(2, novo.getId());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("ERRO >> Atualização falhou.");
            }
        }
    }
}

package br.ufrn.imd.bd.dao;

import br.ufrn.imd.bd.dao.util.ResultSetUtil;
import br.ufrn.imd.bd.model.Funcionario;
import br.ufrn.imd.bd.model.Mesa;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.LocalDate;

@Component
public class MesaDAO extends AbstractDAO<Mesa, Long> {

    @Override
    public Mesa mapearResultado(ResultSet rs) throws SQLException {
        return this.mapearResultado(rs, "");
    }


    public Mesa mapearResultado(ResultSet rs, String prefixo) throws SQLException {
        Mesa mesa = new Mesa();
        mesa.setId(rs.getLong(prefixo + "id_mesa"));
        mesa.setIdentificacao(rs.getString(prefixo + "identificacao"));
        mesa.setAtivo(ResultSetUtil.getBooleanFromInteger(rs,  "is_ativo", prefixo));
        return mesa;
    }

    @Override
    public String getNomeTabela() {
        return "mesa";
    }


    @Override
    public Mesa salvar(Connection conn, Mesa mesa) throws SQLException {
        String sql = String.format("INSERT INTO %s (identificacao) VALUES (?)", getNomeTabela());

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, mesa.getIdentificacao());

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
                "UPDATE %s SET identificacao = ?, is_ativo = ? WHERE id_mesa = ?",
                getNomeTabela()
        );

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, novo.getIdentificacao());
            stmt.setBoolean(2, novo.getAtivo());
            stmt.setLong(3, novo.getId());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("ERRO >> Atualização falhou.");
            }
        }
    }

    public Mesa buscarPorIdentificacao(Connection conn, String identificacao) throws SQLException {
        String sql = String.format("SELECT * FROM %s WHERE identificacao = ?", getNomeTabela());

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, identificacao);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultado(rs);
                }
            }
        }
        return null;
    }

}


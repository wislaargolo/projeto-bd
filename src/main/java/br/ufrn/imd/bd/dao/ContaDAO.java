package br.ufrn.imd.bd.dao;

import br.ufrn.imd.bd.model.Conta;
import br.ufrn.imd.bd.model.enums.MetodoPagamento;
import br.ufrn.imd.bd.model.enums.StatusConta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class ContaDAO extends AbstractDAO<Conta, Long> {

    @Autowired
    private AtendenteDAO atendenteDAO;

    @Autowired
    private CaixaDAO caixaDAO;

    @Autowired
    private MesaDAO mesaDAO;

    @Override
    public String getNomeTabela() {
        return "contas";
    }

    @Override
    public Conta mapearResultado(ResultSet rs) throws SQLException {
        Conta conta = new Conta();
        conta.setId(rs.getLong("id"));
        conta.setAtendente(atendenteDAO.buscarPorId(rs.getLong("id_atendente")));
        conta.setStatusConta(StatusConta.valueOf(rs.getString("status")));
        conta.setCaixa(caixaDAO.buscarPorId(rs.getLong("id_caixa")));
        conta.setMesa(mesaDAO.buscarPorId(rs.getLong("id_mesa")));
        conta.setDataFinalizacao(rs.getObject("data_hora_finalizacao",LocalDateTime.class));
        conta.setAtivo(rs.getBoolean("is_ativo"));
        String metodoPagamento = rs.getString("metodo_pagamento");
        if (metodoPagamento != null && !metodoPagamento.isEmpty()) {
            conta.setMetodoPagamento(MetodoPagamento.valueOf(metodoPagamento));
        } else {
            conta.setMetodoPagamento(null);
        }
        return conta;
    }


    @Override
    protected String getBuscarTodosQuery() {
        return String.format("SELECT * FROM %s WHERE is_ativo = true", getNomeTabela());
    }

    @Override
    public Conta salvar(Connection conn, Conta conta) throws SQLException {
        String sql = String.format("INSERT INTO %s (id_caixa, id_atendente, id_mesa, status, " +
                                   "metodo_pagamento, data_hora_finalizacao, is_ativo) VALUES (?, ?, ?, ?, ?, ?, ?)", getNomeTabela());

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, conta.getCaixa() != null ? conta.getCaixa().getId() : null);
            stmt.setLong(2, conta.getAtendente() != null ? conta.getAtendente().getId() : null);
            stmt.setLong(3, conta.getMesa().getId());
            stmt.setString(4, conta.getStatusConta().toString());
            stmt.setString(5, conta.getMetodoPagamento() != null ? conta.getMetodoPagamento().toString() : null);
            stmt.setTimestamp(6, Timestamp.valueOf(conta.getDataFinalizacao()));
            stmt.setBoolean(7, conta.getAtivo());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("ERRO >> A inserção de Conta falhou, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    conta.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("ERRO >> A inserção de Conta falhou, nenhum ID gerado.");
                }
            }
        }

        return conta;
    }

    @Override
    public void atualizar(Connection conn, Conta... contas) throws SQLException {
        if (contas.length != 1) {
            throw new IllegalArgumentException("ERRO >> Apenas uma Conta para atualização.");
        }

        Conta novo = contas[0];

        String sql = String.format(
                "UPDATE %s SET id_mesa = ?, metodo_pagamento = ?, status = ?, is_ativo = ? WHERE id = ?",
                getNomeTabela()
        );

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, novo.getMesa().getId());
            stmt.setString(2, novo.getMetodoPagamento() != null ? novo.getMetodoPagamento().toString() : null);
            stmt.setString(3, novo.getStatusConta().toString());
            stmt.setBoolean(4, novo.getAtivo());
            stmt.setLong(5, novo.getId());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("ERRO >> Atualização falhou.");
            }
        }
    }
}

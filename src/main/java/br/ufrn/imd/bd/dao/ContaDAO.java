package br.ufrn.imd.bd.dao;

import br.ufrn.imd.bd.dao.util.ResultSetUtil;
import br.ufrn.imd.bd.model.Conta;
import br.ufrn.imd.bd.model.enums.MetodoPagamento;
import br.ufrn.imd.bd.model.enums.ProgressoPedido;
import br.ufrn.imd.bd.model.enums.StatusConta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class ContaDAO extends AbstractDAO<Conta, Long> {

    @Autowired
    private FuncionarioDAO funcionarioDAO;
    @Autowired
    private AtendenteDAO atendenteDAO;

    @Autowired
    private CaixaDAO caixaDAO;

    @Autowired
    private MesaDAO mesaDAO;

    @Override
    public String getNomeTabela() {
        return "conta";
    }

    @Override
    public Conta mapearResultado(ResultSet rs) throws SQLException {
        Conta conta = new Conta();
        conta.setId(rs.getLong("id_conta"));
        conta.setStatusConta(ResultSetUtil.getEnumValue(rs, "status", StatusConta.class));
        conta.setDataFinalizacao(ResultSetUtil.getLocalDate(rs, "data_hora_finalizacao"));
        conta.setCaixa(ResultSetUtil.getEntity(rs, caixaDAO, "caixa_", "id_funcionario"));
        conta.setAtendente(ResultSetUtil.getEntity(rs, atendenteDAO, "atendente_", "id_funcionario"));
        conta.setMesa(ResultSetUtil.getEntity(rs, mesaDAO,"id_mesa"));
        conta.setMetodoPagamento(ResultSetUtil.getEnumValue(rs, "metodo_pagamento", MetodoPagamento.class));
        conta.setTotal(ResultSetUtil.getDouble(rs, "total"));

        return conta;
    }


    @Override
    protected String getBuscarTodosQuery() {
        return "SELECT conta.id_conta, conta.status, conta.metodo_pagamento, conta.data_hora_finalizacao, " +
                "f_atendente.id_funcionario AS atendente_id_funcionario, " +
                "f_atendente.nome AS atendente_nome, " +
                "f_caixa.id_funcionario AS caixa_id_funcionario, " +
                "f_caixa.nome AS caixa_nome, " +
                "conta.id_mesa, " +
                "mesa.identificacao, " +
                "mesa.is_ativo, " +
                "SUM(ppi.quantidade * ip.valor) AS total " +
                "FROM conta " +
                "LEFT OUTER JOIN atendente ON conta.id_atendente = atendente.id_funcionario " +
                "LEFT OUTER JOIN funcionario AS f_atendente ON atendente.id_funcionario = f_atendente.id_funcionario\n" +
                "LEFT OUTER JOIN caixa ON conta.id_caixa = caixa.id_funcionario " +
                "LEFT OUTER JOIN funcionario AS f_caixa ON caixa.id_funcionario = f_caixa.id_funcionario " +
                "JOIN mesa ON conta.id_mesa = mesa.id_mesa " +
                "JOIN pedido AS p ON p.id_conta = conta.id_conta " +
                "JOIN pedido_possui_instancia AS ppi ON ppi.id_pedido = p.id_pedido " +
                "JOIN instancia_produto AS ip ON ip.id_instancia_produto = ppi.id_instancia_produto " +
                "GROUP BY conta.id_conta;";
    }


    @Override
    protected String getBuscarPorIdQuery() {
        return "SELECT * FROM conta NATURAL JOIN mesa WHERE id_conta = ?";
    }

    public Conta buscarPorMesa(Long idMesa) throws SQLException {
        String sql = "SELECT * FROM conta NATURAL JOIN mesa WHERE id_mesa = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idMesa);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultado(rs);
                }
            }
        }
        return null;
    }


    /*@Override
    public Conta salvar(Connection conn, Conta conta) throws SQLException {
        String sql = String.format("INSERT INTO %s (id_caixa, id_atendente, id_mesa, status, " +
                                   "metodo_pagamento, data_hora_finalizacao) VALUES (?, ?, ?, ?, ?, ?)", getNomeTabela());

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setObject(1, conta.getCaixa() != null ? conta.getCaixa().getId() : null);
            stmt.setObject(2, conta.getAtendente() != null ? conta.getAtendente().getId() : null);
            stmt.setLong(3, conta.getMesa().getId());
            stmt.setString(4, conta.getStatusConta().toString());
            stmt.setString(5, conta.getMetodoPagamento() != null ? conta.getMetodoPagamento().toString() : null);
            stmt.setTimestamp(6, conta.getDataFinalizacao() != null ? Timestamp.valueOf(conta.getDataFinalizacao()) : null); //stmt.setTimestamp(6, Timestamp.valueOf(conta.getDataFinalizacao()));

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
    }*/

    @Override
    public Conta salvar(Connection conn, Conta conta) throws SQLException {
        String sql = String.format("INSERT INTO %s (id_mesa) VALUES (?)", getNomeTabela());

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, conta.getMesa().getId());

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
    public void atualizar(Connection conn, Conta... conta) throws SQLException {
        if (conta.length != 1) {
            throw new IllegalArgumentException("ERRO >> Apenas uma Conta para atualização.");
        }

        Conta novo = conta[0];
        LocalDateTime dataHoraFinalizacao = null;

        // Verifica se o status é FINALIZADA para atribuir a data_hora_finalizacao
        if (novo.getStatusConta() == StatusConta.FINALIZADA) {
            dataHoraFinalizacao = LocalDateTime.now();
        }

        String sql = String.format(
                "UPDATE %s SET metodo_pagamento = ?, status = ?, data_hora_finalizacao = ? WHERE id_conta = ?",
                getNomeTabela()
        );

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, novo.getMetodoPagamento() != null ? novo.getMetodoPagamento().toString() : null);
            stmt.setString(2, novo.getStatusConta().toString());
            if (dataHoraFinalizacao != null) {
                stmt.setTimestamp(3, Timestamp.valueOf(dataHoraFinalizacao));
            } else {
                stmt.setNull(3, java.sql.Types.TIMESTAMP);
            }
            stmt.setLong(4, novo.getId());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("ERRO >> Atualização falhou.");
            }
        }
    }

    public Double obterTotalConta(Long id) throws SQLException {
        String sql = "SELECT SUM(ppi.quantidade * ip.valor) AS total_conta FROM conta AS c " +
                "JOIN pedido AS p ON c.id_conta = p.id_conta " +
                "JOIN pedido_possui_instancia AS ppi ON p.id_pedido = ppi.id_pedido " +
                "JOIN instancia_produto AS ip ON ip.id_instancia_produto = ppi.id_instancia_produto " +
                "WHERE c.id_conta = ? AND p.progresso != 'CANCELADO' " +
                "GROUP BY c.id_conta";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total_conta");
            }
        }
        return null;
    }
}

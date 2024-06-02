package br.ufrn.imd.bd.dao;

import br.ufrn.imd.bd.model.Pedido;
import br.ufrn.imd.bd.model.enums.StatusPedido;
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
public class PedidoDAO extends AbstractDAO<Pedido, Long> {

    @Autowired
    private AtendenteDAO atendenteDAO;

    @Autowired
    private ContaDAO contaDAO;

    @Override
    public Pedido mapearResultado(ResultSet rs) throws SQLException {
        Pedido pedido = new Pedido();
        pedido.setId(rs.getLong("id_pedido"));
        pedido.setAtendente(atendenteDAO.buscarPorId(rs.getLong("id_atendente")));
        pedido.setConta(contaDAO.buscarPorId(rs.getLong("id_conta")));
        pedido.setStatusPedido(StatusPedido.valueOf(rs.getString("status")));
        pedido.setDataRegistro(rs.getObject("data_hora_registro", LocalDateTime.class));
        pedido.setAtivo(rs.getBoolean("is_ativo"));

        return pedido;
    }

    @Override
    public String getNomeTabela() {
        return "pedido";
    }

    @Override
    protected String getBuscarTodosQuery() {

        return "SELECT * FROM pedido AS p " +
                "JOIN atendente AS a ON p.id_atendente = a.id_funcionario " +
                "JOIN conta AS c ON p.id_conta = c.id_conta " +
                "JOIN funcionario AS f ON a.id_funcionario = f.id_funcionario";
    }

    @Override
    protected String getBuscarPorIdQuery() {

        return "SELECT * FROM pedido AS p " +
                "JOIN atendente AS a ON p.id_atendente = a.id_funcionario " +
                "JOIN conta AS c ON p.id_conta = c.id_conta " +
                "JOIN funcionario AS f ON a.id_funcionario = f.id_funcionario" +
                "WHERE p.id = ?";
    }


    @Override
    public Pedido salvar(Connection conn, Pedido pedido) throws SQLException {
        String sql = String.format("INSERT INTO %s (id_atendente, id_conta, status, data_hora_registro, " +
                     "is_ativo) VALUES (?, ?, ?, ?, ?)", getNomeTabela());

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, pedido.getAtendente() != null ? pedido.getAtendente().getId() : null);
            stmt.setLong(2, pedido.getConta() != null ? pedido.getConta().getId() : null);
            stmt.setString(3, pedido.getStatusPedido().toString());
            stmt.setTimestamp(4, Timestamp.valueOf(pedido.getDataRegistro()));
            stmt.setBoolean(5, pedido.getAtivo());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("ERRO >> A inserção de Pedido falhou, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    pedido.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("ERRO >> A inserção de Pedido falhou, nenhum ID gerado.");
                }
            }
        }

        return pedido;
    }

    @Override
    public void atualizar(Connection conn, Pedido... pedidos) throws SQLException {

        if (pedidos.length != 1) {
            throw new IllegalArgumentException("ERRO >> Apenas um Pedido para atualização.");
        }

        Pedido novo = pedidos[0];

        String sql = String.format(
                "UPDATE %s SET status = ?, is_ativo = ? WHERE id_pedido = ?",
                getNomeTabela()
        );

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, novo.getStatusPedido().toString());
            stmt.setBoolean(2, novo.getAtivo());
            stmt.setLong(3, novo.getId());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("ERRO >> Atualização falhou.");
            }
        }
    }
}

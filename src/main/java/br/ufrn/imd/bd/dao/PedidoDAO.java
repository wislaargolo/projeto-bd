package br.ufrn.imd.bd.dao;

import br.ufrn.imd.bd.model.InstanciaProduto;
import br.ufrn.imd.bd.model.Pedido;
import br.ufrn.imd.bd.model.PedidoInstancia;
import br.ufrn.imd.bd.model.enums.ProgressoPedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class PedidoDAO extends AbstractDAO<Pedido, Long> {

    @Autowired
    private AtendenteDAO atendenteDAO;

    @Autowired
    private InstanciaProdutoDAO instanciaProdutoDAO;

    @Autowired
    private ContaDAO contaDAO;

    @Override
    public Pedido mapearResultado(ResultSet rs) throws SQLException {
        Pedido pedido = new Pedido();
        pedido.setId(rs.getLong("id_pedido"));
        pedido.setAtendente(atendenteDAO.mapearResultado(rs));
        pedido.setConta(contaDAO.mapearResultado(rs));
        pedido.setProgressoPedido(ProgressoPedido.valueOf(rs.getString("progresso")));
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

        return "SELECT p.*, a.*, f.*, c.status FROM pedido AS p " +
                "JOIN atendente AS a ON p.id_atendente = a.id_funcionario " +
                "JOIN conta AS c ON p.id_conta = c.id_conta " +
                "JOIN funcionario AS f ON a.id_funcionario = f.id_funcionario";
    }

    @Override
    protected String getBuscarPorIdQuery() {

        return "SELECT p.*, a.*, f.*, c.status FROM pedido AS p " +
                "JOIN atendente AS a ON p.id_atendente = a.id_funcionario " +
                "JOIN conta AS c ON p.id_conta = c.id_conta " +
                "JOIN funcionario AS f ON a.id_funcionario = f.id_funcionario" +
                "WHERE p.id = ?";
    }


    @Override
    public Pedido salvar(Connection conn, Pedido pedido) throws SQLException {
        String sql = String.format("INSERT INTO %s (id_atendente, id_conta, progresso) VALUES (?, ?, ?)", getNomeTabela());

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setObject(1, pedido.getAtendente() != null ? pedido.getAtendente().getId() : null);
            stmt.setObject(2, pedido.getConta() != null ? pedido.getConta().getId() : null);
            stmt.setString(3, pedido.getProgressoPedido().toString());

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

            if (pedido.getProdutos() != null && !pedido.getProdutos().isEmpty()) {
                salvarInstanciaEmPedido(conn, pedido);
            }
        }

        return pedido;
    }

    public void salvarInstanciaEmPedido(Connection conn, Pedido pedido) throws SQLException {

        String sql = "INSERT INTO pedido_possui_instancia(id_pedido, id_instancia_produto, quantidade) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (PedidoInstancia pedidoInstancia : pedido.getProdutos()) {
                stmt.setLong(1, pedido.getId());
                stmt.setLong(2, pedidoInstancia.getInstanciaProduto().getId());
                stmt.setInt(3, pedidoInstancia.getQuantidade());
            }


            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("ERRO >> A inserção de Produto em Pedido falhou, nenhuma linha afetada.");
            }
        }
    }

    private List<PedidoInstancia> carregarProdutosDoPedido(Connection conn, Long idPedido) throws SQLException {
        List<PedidoInstancia> produtos = new ArrayList<>();

        String sql = "SELECT * FROM instancia_produto ip " +
                "NATURAL JOIN pedido_possui_instancia " +
                "NATURAL JOIN produto " +
                "WHERE ppi.id_pedido = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idPedido);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                InstanciaProduto produto = instanciaProdutoDAO.mapearResultado(rs);
                PedidoInstancia produtoPedido = new PedidoInstancia();
                produtoPedido.setInstanciaProduto(produto);
                produtoPedido.setQuantidade(rs.getInt("quantidade"));

                produtos.add(produtoPedido);
            }
        }

        return produtos;
    }

    @Override
    public void atualizar(Connection conn, Pedido... pedidos) throws SQLException {

        if (pedidos.length != 1) {
            throw new IllegalArgumentException("ERRO >> Apenas um Pedido para atualização.");
        }

        Pedido novo = pedidos[0];

        String sql = String.format(
                "UPDATE %s SET progresso = ?, is_ativo = ? WHERE id_pedido = ?",
                getNomeTabela()
        );

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, novo.getProgressoPedido().toString());
            stmt.setBoolean(2, novo.getAtivo());
            stmt.setLong(3, novo.getId());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("ERRO >> Atualização falhou.");
            }
        }
    }
}

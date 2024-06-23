package br.ufrn.imd.bd.dao;

import br.ufrn.imd.bd.dao.util.ResultSetUtil;
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
        pedido.setAtendente(ResultSetUtil.getEntity(rs, atendenteDAO, "atendente_pedido_", "id_funcionario"));
        pedido.setConta(ResultSetUtil.getEntity(rs, contaDAO, "id_conta"));
        pedido.setProgressoPedido(ProgressoPedido.valueOf(rs.getString("progresso")));
        pedido.setDataRegistro(ResultSetUtil.getLocalDate(rs, "data_hora_registro"));

        return pedido;
    }

    public PedidoInstancia mapearPedidoInstancia(ResultSet rs) throws SQLException {
        PedidoInstancia pedidoInstancia = new PedidoInstancia();
        pedidoInstancia.setInstanciaProduto(ResultSetUtil.getEntity(rs, instanciaProdutoDAO, "id_instancia_produto"));
        pedidoInstancia.setQuantidade(ResultSetUtil.getValue(rs, "quantidade", Integer.class));

        return pedidoInstancia;
    }

    @Override
    public String getNomeTabela() {
        return "pedido";
    }

    // modifica de acordo com que quiser
    @Override
    protected String getBuscarTodosQuery() {

          return "SELECT p.*, a.*, f.* FROM pedido AS p " +
                "JOIN atendente AS a ON p.id_atendente = a.id_funcionario " +
                "NATURAL JOIN funcionario AS f";
    }

    @Override
    protected String getBuscarPorIdQuery() {
        return  "SELECT id_pedido, progresso, data_hora_registro FROM pedido WHERE id_pedido = ?";
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
                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("ERRO >> A inserção de Produto em Pedido falhou, nenhuma linha afetada.");
                }
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
                "UPDATE %s SET progresso = ? WHERE id_pedido = ?",
                getNomeTabela()
        );

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, novo.getProgressoPedido().toString());
            stmt.setLong(2, novo.getId());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("ERRO >> Atualização falhou.");
            }
        }
    }

    public Pedido buscarPorIdComProdutos(Long id) throws SQLException {
        String sql = "SELECT p.id_conta, p.id_pedido, p.progresso, " +
                "ppi.quantidade, ip.*, produto.descricao, m.*, " +
                "f.nome AS atendente_pedido_nome, " +
                "a.tipo AS atendente_pedido_tipo, " +
                "f.id_funcionario AS atendente_pedido_id_funcionario " +
                "FROM pedido p " +
                "JOIN atendente AS a ON p.id_atendente = a.id_funcionario " +
                "JOIN funcionario AS f ON a.id_funcionario = f.id_funcionario " +
                "JOIN conta AS c ON p.id_conta = c.id_conta " +
                "JOIN mesa AS m ON c.id_mesa = m.id_mesa " +
                "LEFT JOIN pedido_possui_instancia AS ppi ON p.id_pedido = ppi.id_pedido " +
                "LEFT JOIN instancia_produto AS ip ON ppi.id_instancia_produto = ip.id_instancia_produto " +
                "LEFT JOIN produto ON ip.id_produto = produto.id_produto WHERE p.id_pedido = ?";
        Pedido pedido = null;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    if (pedido == null) {
                        pedido = mapearResultado(rs);
                    }

                    PedidoInstancia pedidoInstancia = mapearPedidoInstancia(rs);
                    if (pedidoInstancia != null && pedidoInstancia.getInstanciaProduto() != null) {
                        pedido.getProdutos().add(pedidoInstancia);
                    }
                }
            }
        }

        return pedido;
    }

    public List<Pedido> buscarPedidosAbertos(Long idMesa) throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();

        StringBuilder sql = new StringBuilder("SELECT p.*, m.*, c.status, " +
                "f.nome AS atendente_pedido_nome, " +
                "f.id_funcionario AS atendente_pedido_id_funcionario, " +
                "f.login AS atendente_pedido_login, " +
                "a.tipo AS atendente_pedido_tipo " +
                "FROM mesa AS m " +
                "JOIN conta AS c ON c.id_mesa = m.id_mesa " +
                "JOIN pedido AS p ON p.id_conta = c.id_conta " +
                "JOIN atendente AS a ON p.id_atendente = a.id_funcionario " +
                "JOIN funcionario AS f ON a.id_funcionario = f.id_funcionario ");

        if (idMesa != null) {
            sql.append("WHERE m.id_mesa = ? AND c.status != 'FINALIZADA' ");
        } else {
            sql.append("WHERE c.status != 'FINALIZADA' ");
        }

        sql.append("ORDER BY p.data_hora_registro DESC");

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            if (idMesa != null) {
                stmt.setLong(1, idMesa);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Pedido pedido = mapearResultado(rs);
                    List<PedidoInstancia> produtos = buscaProdutosPedido(conn, pedido.getId());
                    pedido.setProdutos(produtos);
                    pedidos.add(pedido);
                }
            }
        }

        return pedidos;
    }

    public List<PedidoInstancia> buscaProdutosPedido(Connection conn, Long idPedido) throws SQLException {
        List<PedidoInstancia> produtos = new ArrayList<>();

        String sql = "SELECT * FROM pedido " +
                "NATURAL JOIN pedido_possui_instancia " +
                "NATURAL JOIN instancia_produto " +
                "NATURAL JOIN produto WHERE id_pedido = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idPedido);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PedidoInstancia produto = mapearPedidoInstancia(rs);
                    produtos.add(produto);
                }
            }
        }

        return produtos;
    }

}

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
        pedido.setAtendente(atendenteDAO.mapearResultado(rs, "atendente_"));
        pedido.setConta(contaDAO.mapearResultado(rs));
        pedido.setProgressoPedido(ProgressoPedido.valueOf(rs.getString("progresso")));
        pedido.setDataRegistro(rs.getObject("data_hora_registro", LocalDateTime.class));

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

    // modifica de acordo com o que quiser
    @Override
    protected String getBuscarPorIdQuery() {

        return  "SELECT * FROM pedido WHERE p.id_pedido = ?";
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

    public List<Pedido> buscarPedidoPorPeriodo(Connection conn, LocalDateTime inicio, LocalDateTime fim) throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();

        String sql = "SELECT p.*, m.*, " +
                "f.is_ativo AS atendente_is_ativo, " +
                "f.nome AS atendente_nome, " +
                "f.email AS atendente_email, " +
                "f.login AS atendente_login, " +
                "f.senha AS atendente_senha, " +
                "a.tipo AS atendente_tipo, " +
                "f.data_cadastro AS atendente_data_cadastro, " +
                "f.id_funcionario AS atendente_id_funcionario " +
                "FROM pedido AS p " +
                "JOIN atendente AS a ON p.id_atendente = a.id_funcionario " +
                "JOIN funcionario AS f ON a.id_funcionario = f.id_funcionario " +
                "JOIN conta AS c ON c.id_conta = p.id_conta " +
                "JOIN mesa AS m ON c.id_mesa = m.id_mesa " +
                "WHERE p.data_hora_registro >= ? " +
                "AND p.data_hora_registro < ? " +
                "ORDER BY p.progresso = 'SOLICITADO' DESC, p.data_hora_registro DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, inicio);
            stmt.setObject(2, fim);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Pedido pedido = mapearResultado(rs);
                pedidos.add(pedido);
            }
        }

        return pedidos;
    }

    public Pedido buscarPorIdComProdutos(Long id) throws SQLException {
        String sql = "SELECT * FROM pedido p " +
                "JOIN atendente AS a ON p.id_atendente = a.id_funcionario " +
                "JOIN funcionario AS f ON a.id_funcionario = f.id_funcionario " +
                "JOIN conta AS c ON p.id_conta = c.id_conta " +
                "JOIN mesa AS m ON c.id_mesa = m.id_mesa " +
                "JOIN pedido_possui_instancia AS ppi ON p.id_pedido = ppi.id_pedido " +
                "JOIN instancia_produto AS ip ON ppi.id_instancia_produto = ip.id_instancia_produto " +
                "JOIN produto ON ip.id_produto = produto.id_produto WHERE p.id_pedido = ?";
        Pedido pedido = new Pedido();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                if (pedido.getId() == null) {
                    pedido = mapearResultado(rs);
                }

                PedidoInstancia pedidoInstancia = mapearPedidoInstancia(rs);
                if (pedidoInstancia != null && pedidoInstancia.getInstanciaProduto() != null) {
                    pedido.getProdutos().add(pedidoInstancia);
                }
            }
        }

        return pedido;
    }

}

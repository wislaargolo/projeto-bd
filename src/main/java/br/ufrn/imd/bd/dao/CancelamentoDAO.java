package br.ufrn.imd.bd.dao;

import br.ufrn.imd.bd.dao.util.ResultSetUtil;
import br.ufrn.imd.bd.model.Cancelamento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class CancelamentoDAO extends AbstractDAO<Cancelamento, Long> {

    @Autowired
    private AtendenteDAO atendenteDAO;

    @Autowired
    private PedidoDAO pedidoDAO;

    @Autowired
    private InstanciaProdutoDAO instanciaProdutoDAO;

    @Override
    public Cancelamento mapearResultado(ResultSet rs) throws SQLException {
        Cancelamento cancelamento = new Cancelamento();
        cancelamento.setId(rs.getLong( "id_cancelamento"));
        cancelamento.setAtendente(atendenteDAO.mapearResultado(rs));
        cancelamento.setPedido(pedidoDAO.mapearResultado(rs));
        cancelamento.setProduto(instanciaProdutoDAO.mapearResultado(rs));
        cancelamento.setDataRegistro(rs.getObject("data_hora", LocalDateTime.class));
        return cancelamento;
    }

    @Override
    public String getNomeTabela() {
        return "cancelamento";
    }

    @Override
    protected String getBuscarPorIdQuery() {
        return  "SELECT * FROM cancelamento WHERE id_cancelamento = ?";
    }

    @Override
    protected String getBuscarTodosQuery() {
        return "SELECT p.progresso, pr.descricao, pr.id_produto, m.*, c.id_conta, " +
                "ca.*, f.id_funcionario, f.nome " +
                "FROM cancelamento AS ca " +
                "JOIN funcionario AS f ON ca.id_atendente = f.id_funcionario " +
                "JOIN pedido AS p ON ca.id_pedido = p.id_pedido " +
                "JOIN instancia_produto AS ip ON ca.id_instancia_produto = ip.id_instancia_produto " +
                "JOIN produto AS pr ON ip.id_produto = pr.id_produto " +
                "JOIN conta AS c ON p.id_conta = c.id_conta " +
                "JOIN mesa AS m ON c.id_mesa = m.id_mesa " +
                "WHERE DATE(ca.data_hora) = CURDATE() " +
                "ORDER BY ca.data_hora DESC;";
    }



    @Override
    public Cancelamento salvar(Connection conn, Cancelamento cancelamento) throws SQLException {
        String sql = String.format("INSERT INTO %s (id_pedido, id_instancia_produto, id_atendente) " +
                                    "VALUES (?, ?, ?)", getNomeTabela());

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, cancelamento.getPedido().getId());
            stmt.setLong(2, cancelamento.getProduto().getId());
            stmt.setLong(3, cancelamento.getAtendente().getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("ERRO >> A inserção de cancelamento falhou, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cancelamento.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("ERRO >> A inserção do cancelamento falhou, nenhum ID gerado.");
                }
            }
        }

        return cancelamento;
    }

    @Override
    public void atualizar(Connection conn, Cancelamento... entidade) throws SQLException {

    }
}

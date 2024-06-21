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
        cancelamento.setQuantidade(rs.getInt("quantidade_cancelada"));
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
    public Cancelamento salvar(Connection conn, Cancelamento cancelamento) throws SQLException {
        String sql = String.format("INSERT INTO %s (id_pedido, id_instancia_produto, id_atendente, quantidade_cancelada) " +
                                    "VALUES (?, ?, ?, ?)", getNomeTabela());

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, cancelamento.getPedido().getId());
            stmt.setLong(2, cancelamento.getProduto().getId());
            stmt.setLong(3, cancelamento.getAtendente().getId());
            stmt.setInt(4, cancelamento.getQuantidade());

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

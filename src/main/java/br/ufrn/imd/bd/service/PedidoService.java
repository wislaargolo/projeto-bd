package br.ufrn.imd.bd.service;

import br.ufrn.imd.bd.connection.DatabaseConfig;
import br.ufrn.imd.bd.dao.ContaDAO;
import br.ufrn.imd.bd.dao.InstanciaProdutoDAO;
import br.ufrn.imd.bd.dao.PedidoDAO;
import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.model.Conta;
import br.ufrn.imd.bd.model.InstanciaProduto;
import br.ufrn.imd.bd.model.Mesa;
import br.ufrn.imd.bd.model.Pedido;
import br.ufrn.imd.bd.model.enums.ProgressoPedido;
import br.ufrn.imd.bd.model.enums.StatusConta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class PedidoService {

    @Autowired
    private PedidoDAO pedidoDAO;

    @Autowired
    private ContaDAO contaDAO;

    public List<Pedido> buscarTodos() throws SQLException {
        return pedidoDAO.buscarTodos();
    }

    public Pedido salvar(Pedido pedido) throws SQLException {

        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            pedido = pedidoDAO.salvar(conn, pedido);
            conn.commit();
        } catch (SQLException e) {
            DatabaseConfig.rollback(conn);
            throw e;
        } finally {
            DatabaseConfig.close(conn);
        }

        return pedido;
    }

    public void atualizar(Pedido pedido) throws EntidadeJaExisteException, SQLException {
        try (Connection conn = DatabaseConfig.getConnection()){
            pedidoDAO.atualizar(conn, pedido);
        }
    }

    public Pedido buscarPorIdComProdutos(Long id) throws SQLException {
        return pedidoDAO.buscarPorIdComProdutos(id);
    }

    public List<Pedido> buscarPedidosPorTurno() throws SQLException {
        LocalDateTime inicioTurno;
        LocalDateTime fimTurno;

        LocalDateTime now = LocalDateTime.now();
        LocalTime nowTime = now.toLocalTime();

        if (nowTime.isAfter(LocalTime.MIDNIGHT) && nowTime.isBefore(LocalTime.NOON)) {
            // Turno da manhã: 00:01 até 12:00
            inicioTurno = now.with(LocalTime.MIN);
            fimTurno = now.with(LocalTime.NOON);
        } else if (nowTime.isAfter(LocalTime.NOON) && nowTime.isBefore(LocalTime.of(17, 0))) {
            // Turno da tarde: 12:01 até 17:00
            inicioTurno = now.with(LocalTime.NOON).plusSeconds(1);
            fimTurno = now.with(LocalTime.of(17, 0));
        } else {
            // Turno da noite: 17:01 até 23:59
            inicioTurno = now.with(LocalTime.of(17, 0)).plusSeconds(1);
            fimTurno = now.with(LocalTime.MAX);
        }

        try (Connection conn = DatabaseConfig.getConnection()) {
            return pedidoDAO.buscarPedidoPorPeriodo(conn, inicioTurno, fimTurno);
        }
    }

}

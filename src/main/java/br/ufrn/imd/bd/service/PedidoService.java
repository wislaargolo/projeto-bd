package br.ufrn.imd.bd.service;

import br.ufrn.imd.bd.connection.DatabaseUtil;
import br.ufrn.imd.bd.dao.ContaDAO;
import br.ufrn.imd.bd.dao.PedidoDAO;
import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.exceptions.EntidadeNaoExisteException;
import br.ufrn.imd.bd.model.Pedido;
import br.ufrn.imd.bd.model.enums.ProgressoPedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
public class PedidoService {

    @Autowired
    private PedidoDAO pedidoDAO;

    @Autowired
    private MesaService mesaService;

    @Autowired
    private DatabaseUtil databaseUtil;

    @Autowired
    private ContaDAO contaDAO;

    public List<Pedido> buscarTodos() throws SQLException {
        return pedidoDAO.buscarTodos();
    }

    public Pedido buscarPorId(Long id) throws SQLException, EntidadeNaoExisteException {
        Pedido pedido = pedidoDAO.buscarPorChave(id);
        if(pedido == null) {
            throw new EntidadeNaoExisteException("Pedido não encontrado");
        }

        return pedido;
    }

    public void atualizar(Pedido pedido) throws EntidadeJaExisteException, SQLException {
        try (Connection conn = databaseUtil.getConnection()){
            pedidoDAO.atualizar(conn, pedido);
        }
    }

    public Pedido buscarPorIdComProdutos(Long id) throws SQLException, EntidadeNaoExisteException {
        Pedido pedido = pedidoDAO.buscarPorIdComProdutos(id);
        if(pedido == null) {
            throw new EntidadeNaoExisteException("Pedido não encontrado");
        }
        return pedido;
    }

    public List<Pedido> buscarPedidosAbertos() throws SQLException {
        return pedidoDAO.buscarPedidosAbertos(null);
    }

    public List<Pedido> buscarPedidosPorMesa(Long idMesa) throws SQLException, EntidadeNaoExisteException {

        mesaService.buscarPorId(idMesa);
        return pedidoDAO.buscarPedidosAbertos(idMesa);
    }

    public void atualizarProdutos(Pedido pedido) throws SQLException {
        Connection conn = null;
        try {
            conn = databaseUtil.getConnection();
            conn.setAutoCommit(false);
            pedidoDAO.atualizarInstanciaEmPedido(conn, pedido);
            conn.commit();
        } catch (SQLException e) {
            databaseUtil.rollback(conn);
            throw e;
        } finally {
            databaseUtil.close(conn);
        }

    }

    public void salvarPedido(Pedido pedido) throws SQLException{
        Connection conn = null;

        try {
            conn = databaseUtil.getConnection();
            conn.setAutoCommit(false);


            if (pedido.getConta().getId() == null) {
                contaDAO.salvar(conn, pedido.getConta());
            }
            pedido.setProgressoPedido(ProgressoPedido.SOLICITADO);
            pedidoDAO.salvar(conn, pedido);

            conn.commit();
        } catch (SQLException e) {
            databaseUtil.rollback(conn);
            throw e;
        } finally {
            databaseUtil.close(conn);
        }
    }



}

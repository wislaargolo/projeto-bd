package br.ufrn.imd.bd.service;

import br.ufrn.imd.bd.connection.DatabaseConfig;
import br.ufrn.imd.bd.dao.CancelamentoDAO;
import br.ufrn.imd.bd.dao.PedidoDAO;
import br.ufrn.imd.bd.exceptions.EntidadeNaoExisteException;
import br.ufrn.imd.bd.model.Atendente;
import br.ufrn.imd.bd.model.Cancelamento;
import br.ufrn.imd.bd.model.Funcionario;
import br.ufrn.imd.bd.model.Pedido;
import br.ufrn.imd.bd.model.PedidoInstancia;
import br.ufrn.imd.bd.model.enums.ProgressoPedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CancelamentoService {

    @Autowired
    private CancelamentoDAO cancelamentoDAO;

    @Autowired
    private PedidoDAO pedidoDAO;

    @Autowired
    private PedidoService pedidoService;

    public Cancelamento buscarPorId(Long id) throws SQLException, EntidadeNaoExisteException {

        Cancelamento cancelamento = cancelamentoDAO.buscarPorChave(id);

        if(cancelamento == null) {
            throw new EntidadeNaoExisteException("Cancelamento não encontrado");
        }
        return cancelamento;

    }

    public List<Cancelamento> buscarTodos() throws SQLException {
        return cancelamentoDAO.buscarTodos();
    }

    public Map<Long, List<Cancelamento>> agruparCancelamentosPorPedido() throws SQLException {
        Map<Long, List<Cancelamento>> agrupadosPorPedido = new HashMap<>();

        for (Cancelamento cancelamento : buscarTodos()) {
            Long pedidoId = cancelamento.getPedido().getId();
            List<Cancelamento> listaCancelados = agrupadosPorPedido.getOrDefault(pedidoId, new ArrayList<>());
            listaCancelados.add(cancelamento);
            agrupadosPorPedido.put(pedidoId, listaCancelados);
        }

        return agrupadosPorPedido;
    }

    public Pedido cancelarItemDoPedido(Long pedidoId, Long produtoId) throws SQLException, EntidadeNaoExisteException {

        Pedido pedido = pedidoService.buscarPorIdComProdutos(pedidoId);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Funcionario funcionarioLogado = (Funcionario) auth.getPrincipal();

        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            List<PedidoInstancia> itensDoPedido = pedidoDAO.buscaProdutosPedido(conn, pedido.getId());
            PedidoInstancia itemParaCancelar = null;

            // Encontrar o item que será cancelado
            for (PedidoInstancia item : itensDoPedido) {
                if (item.getInstanciaProduto().getProduto().getId().equals(produtoId)) {
                    itemParaCancelar = item;
                    break;
                }
            }

            if (itemParaCancelar == null) {
                throw new EntidadeNaoExisteException("O produto especificado não está no pedido.");
            }

            // Criar um novo cancelamento para o item
            Cancelamento cancelamento = new Cancelamento();
            cancelamento.setPedido(pedido);
            cancelamento.setProduto(itemParaCancelar.getInstanciaProduto());
            cancelamento.setAtendente(new Atendente(funcionarioLogado.getId()));
            cancelamento.setQuantidade(itemParaCancelar.getQuantidade());

            cancelamentoDAO.salvar(conn, cancelamento);

            // Remover o item do pedido
            int index = 0;

            for (PedidoInstancia item : pedido.getProdutos()) {
                if (item.getInstanciaProduto().getProduto().getId().equals(itemParaCancelar.getInstanciaProduto().getProduto().getId())) {
                    pedido.getProdutos().remove(index);
                    break;
                }
                index++;
            }
            pedidoService.atualizarProdutos(pedido);
            conn.commit();
        } catch (SQLException e) {
            DatabaseConfig.rollback(conn);
            throw e;
        } finally {
            DatabaseConfig.close(conn);
        }
        return pedido;
    }

    public Pedido cancelarPedido(Long id) throws SQLException, EntidadeNaoExisteException {

        Pedido pedido = pedidoService.buscarPorId(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Funcionario funcionarioLogado = (Funcionario) auth.getPrincipal();

        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            List<PedidoInstancia> itensDoPedido = pedidoDAO.buscaProdutosPedido(conn, pedido.getId());

            for (PedidoInstancia item : itensDoPedido) {
                Cancelamento cancelamento = new Cancelamento();
                cancelamento.setPedido(pedido);
                cancelamento.setProduto(item.getInstanciaProduto());
                cancelamento.setAtendente(new Atendente(funcionarioLogado.getId()));
                cancelamento.setQuantidade(item.getQuantidade());

                cancelamentoDAO.salvar(conn, cancelamento);
            }

            pedido.setProgressoPedido(ProgressoPedido.CANCELADO);
            pedidoDAO.atualizar(conn, pedido);

            conn.commit();
        } catch (SQLException e) {
            DatabaseConfig.rollback(conn);
            throw e;
        } finally {
            DatabaseConfig.close(conn);
        }
        return pedido;
    }
}

package br.ufrn.imd.bd.service;

import br.ufrn.imd.bd.dao.InstanciaProdutoDAO;
import br.ufrn.imd.bd.dao.PedidoDAO;
import br.ufrn.imd.bd.model.InstanciaProduto;
import br.ufrn.imd.bd.model.Pedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class PedidoService {

    @Autowired
    private PedidoDAO pedidoDAO;

    public List<Pedido> buscarTodos() throws SQLException {
        return pedidoDAO.buscarTodos();
    }
}

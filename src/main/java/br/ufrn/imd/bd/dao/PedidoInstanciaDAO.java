
package br.ufrn.imd.bd.dao;
import br.ufrn.imd.bd.model.PedidoInstancia;
import br.ufrn.imd.bd.model.PedidoInstanciaKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class PedidoInstanciaDAO extends AbstractDAO<PedidoInstancia, PedidoInstanciaKey>{

    @Autowired
    private PedidoDAO pedidoDAO;

    @Autowired
    private InstanciaProdutoDAO instanciaProdutoDAO;
    @Override
    public PedidoInstancia mapearResultado(ResultSet rs) throws SQLException {
        PedidoInstancia pedidoInstancia = new PedidoInstancia();
        pedidoInstancia.setPedido(pedidoDAO.buscarPorId(rs.getLong("id_pedido")));
        pedidoInstancia.setInstanciaProduto(instanciaProdutoDAO.buscarPorId(rs.getLong("id_instancia_produto")));
        pedidoInstancia.setQuantidade(rs.getInt("quantidade"));
        pedidoInstancia.setAtivo(rs.getBoolean("is_ativo"));
        return pedidoInstancia;
    }

    @Override
    public String getNomeTabela() {
        return "pedido_possui_instancia";
    }

    @Override
    protected String getBuscarTodosQuery() {
        return String.format("SELECT * FROM %s WHERE is_ativo = true", getNomeTabela());
    }

    @Override
    public PedidoInstancia salvar(Connection conn, PedidoInstancia entidade) throws SQLException {
        return null;
    }

    @Override
    public void atualizar(Connection conn, PedidoInstancia... entidade) throws SQLException {

    }
}

package br.ufrn.imd.bd.model;

import jakarta.validation.constraints.NotNull;

public class PedidoInstanciaProduto {

    @NotNull
    private Pedido pedido;

    @NotNull
    private InstanciaProduto instanciaProduto;

    @NotNull
    private Integer quantidade;


    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public InstanciaProduto getInstanciaProduto() {
        return instanciaProduto;
    }

    public void setInstanciaProduto(InstanciaProduto instanciaProduto) {
        this.instanciaProduto = instanciaProduto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}

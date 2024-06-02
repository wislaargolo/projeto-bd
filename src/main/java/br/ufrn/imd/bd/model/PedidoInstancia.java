package br.ufrn.imd.bd.model;

import jakarta.validation.constraints.NotNull;

public class PedidoInstancia {

    @NotNull
    private Pedido pedido;

    @NotNull
    private InstanciaProduto instanciaProduto;

    @NotNull
    private Integer quantidade;

    private Boolean isAtivo;


    public PedidoInstanciaKey getKey() {
        return new PedidoInstanciaKey(this.pedido.getId(), this.instanciaProduto.getId());
    }

    public Boolean getAtivo() {
        return isAtivo;
    }

    public void setAtivo(Boolean ativo) {
        isAtivo = ativo;
    }

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

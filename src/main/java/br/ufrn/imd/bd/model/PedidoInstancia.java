package br.ufrn.imd.bd.model;

import jakarta.validation.constraints.NotNull;

public class PedidoInstancia {

    private InstanciaProduto instanciaProduto;

    private Integer quantidade;

    public PedidoInstancia() {}

    public PedidoInstancia(InstanciaProduto instanciaProduto, Integer quantidade) {
        this.instanciaProduto = instanciaProduto;
        this.quantidade = quantidade;
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

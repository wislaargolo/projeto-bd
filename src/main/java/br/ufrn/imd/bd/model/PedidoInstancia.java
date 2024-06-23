package br.ufrn.imd.bd.model;

import jakarta.validation.constraints.NotNull;

import java.util.Objects;

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

    //coloquei pra poder usar o contains
    /*@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PedidoInstancia that = (PedidoInstancia) o;
        return Objects.equals(instanciaProduto, that.instanciaProduto) &&
                Objects.equals(quantidade, that.quantidade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instanciaProduto, quantidade);
    }*/
}

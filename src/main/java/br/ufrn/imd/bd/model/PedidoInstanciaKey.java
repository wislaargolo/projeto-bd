package br.ufrn.imd.bd.model;

import java.util.Objects;

public class PedidoInstanciaKey {

    private Long idPedido;
    private Long idInstanciaProduto;

    public PedidoInstanciaKey(Long idPedido, Long idInstanciaProduto) {
        this.idPedido = idPedido;
        this.idInstanciaProduto = idInstanciaProduto;
    }

    public Long getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Long idPedido) {
        this.idPedido = idPedido;
    }

    public Long getIdInstanciaProduto() {
        return idInstanciaProduto;
    }

    public void setIdInstanciaProduto(Long idInstanciaProduto) {
        this.idInstanciaProduto = idInstanciaProduto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PedidoInstanciaKey that)) return false;
        return Objects.equals(idPedido, that.idPedido) && Objects.equals(idInstanciaProduto, that.idInstanciaProduto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPedido, idInstanciaProduto);
    }
}

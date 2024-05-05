package br.ufrn.imd.bd.model;

public class PedidoInstancia {

    private Long idPedido;

    private Long idInstanciaProduto;

    private Integer quantidade;

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

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}

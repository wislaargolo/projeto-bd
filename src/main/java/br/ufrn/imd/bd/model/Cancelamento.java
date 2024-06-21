package br.ufrn.imd.bd.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Cancelamento {
    private Long id;

    private Pedido pedido;

    private InstanciaProduto produto;

    private Atendente atendente;

    private LocalDateTime dataRegistro;

    private Integer quantidade;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public InstanciaProduto getProduto() {
        return produto;
    }

    public void setProduto(InstanciaProduto produto) {
        this.produto = produto;
    }

    public Atendente getAtendente() {
        return atendente;
    }

    public void setAtendente(Atendente atendente) {
        this.atendente = atendente;
    }

    public LocalDateTime getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(LocalDateTime dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}

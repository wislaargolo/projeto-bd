package br.ufrn.imd.bd.model;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class InstanciaProduto {

    private Long id;

    @NotNull
    private Double valor;

    @NotNull
    private Boolean isAtivo;

    private LocalDateTime data;

    private Produto produto;

    public InstanciaProduto() {
        this.setAtivo(true);
        this.produto = new Produto();
        produto.setDisponivel(true);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public Boolean getAtivo() {
        return isAtivo;
    }

    public void setAtivo(Boolean ativo) {
        isAtivo = ativo;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }
}

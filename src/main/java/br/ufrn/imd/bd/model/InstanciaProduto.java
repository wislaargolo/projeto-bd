package br.ufrn.imd.bd.model;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class InstanciaProduto {

    private Long id;

    @NotNull
    private Double valor;

    @NotNull
    private Boolean isDisponivel;

    private LocalDateTime data;

    private Long produtoId;

    public InstanciaProduto() {
        this.setDisponivel(true);
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

    public Boolean getDisponivel() {
        return isDisponivel;
    }

    public void setDisponivel(Boolean disponivel) {
        isDisponivel = disponivel;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }
}
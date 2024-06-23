package br.ufrn.imd.bd.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;

public class InstanciaProduto {

    private Long id;


    @NotNull(message = "Valor é obrigatório.")
    private Double valor;

    private Boolean isAtivo;

    private LocalDateTime data;

    @Valid
    private Produto produto;

    public InstanciaProduto() {
        this.produto = new Produto();
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

    //coloquei pra poder usar o contains em PedidoInstacia
   /* @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstanciaProduto that = (InstanciaProduto) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(valor, that.valor) &&
                Objects.equals(isAtivo, that.isAtivo) &&
                Objects.equals(data, that.data) &&
                Objects.equals(produto, that.produto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, valor, isAtivo, data, produto);
    }*/
}

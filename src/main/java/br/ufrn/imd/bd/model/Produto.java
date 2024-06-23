package br.ufrn.imd.bd.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public class Produto {

    private Long id;

    @NotBlank(message = "Descrição é obrigatória.")
    private String descricao;

    @NotNull(message = "Disponibilidade é obrigatória.")
    private Boolean disponivel;

    public Produto() {}

    public Produto(String descricao, Boolean isDisponivel) {
        this.descricao = descricao;
        this.disponivel = isDisponivel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getDisponivel() {
        return disponivel;
    }

    public void setDisponivel(Boolean disponivel) {
        this.disponivel = disponivel;
    }

    //coloquei pra poder usar o contains em PedidoInstancia
    /*@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Produto produto = (Produto) o;
        return Objects.equals(id, produto.id) &&
                Objects.equals(descricao, produto.descricao) &&
                Objects.equals(disponivel, produto.disponivel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, descricao, disponivel);
    }*/

}
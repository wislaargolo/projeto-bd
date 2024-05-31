package br.ufrn.imd.bd.model;

import jakarta.validation.constraints.NotBlank;

public class Produto {

    private Long id;

    @NotBlank
    private String nome;

    private Boolean isDisponivel;

    public Produto() {}

    public Produto(String nome, Boolean isDisponivel) {
        this.nome = nome;
        this.isDisponivel = isDisponivel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Boolean getDisponivel() {
        return isDisponivel;
    }

    public void setDisponivel(Boolean disponivel) {
        isDisponivel = disponivel;
    }
}
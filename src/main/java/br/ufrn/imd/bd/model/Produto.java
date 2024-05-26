package br.ufrn.imd.bd.model;

import br.ufrn.imd.bd.dao.ProdutoDAO;
import jakarta.validation.constraints.NotBlank;

public class Produto {

    private Long id;

    @NotBlank
    private String nome;

    private Boolean isAtivo;

    public Produto() {}

    public Produto(String nome, Boolean isAtivo) {
        this.nome = nome;
        this.isAtivo = isAtivo;
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

    public Boolean getAtivo() {
        return isAtivo;
    }

    public void setAtivo(Boolean ativo) {
        isAtivo = ativo;
    }
}

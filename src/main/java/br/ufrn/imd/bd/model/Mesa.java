package br.ufrn.imd.bd.model;

import jakarta.validation.constraints.NotBlank;

public class Mesa {

    private Long id;

    @NotBlank(message = "Identificação é obrigatória")
    private String identificacao;

    private Boolean ativo;

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentificacao() {
        return identificacao;
    }

    public void setIdentificacao(String identificacao) {
        this.identificacao = identificacao;
    }
}

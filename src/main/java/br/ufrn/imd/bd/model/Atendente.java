package br.ufrn.imd.bd.model;

import jakarta.validation.constraints.NotNull;

public class Atendente extends Funcionario {

    @NotNull
    private TipoAtendente tipo;

    public Atendente(Funcionario funcionario) {
        super();
        this.setId(funcionario.getId());
        this.setNome(funcionario.getNome());
        this.setLogin(funcionario.getLogin());
        this.setSenha(funcionario.getSenha());
        this.setEmail(funcionario.getEmail());
        this.setDataCadastro(funcionario.getDataCadastro());
    }

    public Atendente() {}

    public TipoAtendente getTipo() {
        return tipo;
    }

    public void setTipo(TipoAtendente tipo) {
        this.tipo = tipo;
    }
}

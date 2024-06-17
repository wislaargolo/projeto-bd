package br.ufrn.imd.bd.model;

import br.ufrn.imd.bd.model.enums.TipoAtendente;
import jakarta.validation.constraints.NotNull;

public class Atendente extends Funcionario {

    private TipoAtendente tipo;

    public Atendente(Funcionario funcionario) {
        super();
        if(funcionario != null) {
            this.setId(funcionario.getId());
            this.setNome(funcionario.getNome());
            this.setLogin(funcionario.getLogin());
            this.setSenha(funcionario.getSenha());
            this.setEmail(funcionario.getEmail());
            this.setDataCadastro(funcionario.getDataCadastro());
            this.setAtivo(funcionario.getAtivo());
        }
    }

    public Atendente() {}

    public TipoAtendente getTipo() {
        return tipo;
    }

    public void setTipo(TipoAtendente tipo) {
        this.tipo = tipo;
    }
}

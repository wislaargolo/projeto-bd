package br.ufrn.imd.bd.model;

import br.ufrn.imd.bd.model.enums.TipoAtendente;

public class Atendente extends Funcionario {

    private TipoAtendente tipo;

    public TipoAtendente getTipo() {
        return tipo;
    }

    public void setTipo(TipoAtendente tipo) {
        this.tipo = tipo;
    }
}

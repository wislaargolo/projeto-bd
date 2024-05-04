package br.ufrn.imd.bd.model;

public class Atendente extends Funcionario {

    private TipoAtendente tipo;

    public TipoAtendente getTipo() {
        return tipo;
    }

    public void setTipo(TipoAtendente tipo) {
        this.tipo = tipo;
    }
}

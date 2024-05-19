package br.ufrn.imd.bd.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class Telefone {

    @NotBlank
    private String telefone;

    private Funcionario funcionario;

    public Telefone() {}

    public Telefone(String telefone, Funcionario funcionario) {
        this.telefone = telefone;
        this.funcionario = funcionario;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }
}

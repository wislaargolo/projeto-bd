package br.ufrn.imd.bd.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class Telefone {

    @Pattern(regexp = "^\\([1-9]{2}\\) 9[0-9]{4}-[0-9]{4}$", message = "Telefone deve estar no formato (00) 90000-0000.")
    @NotBlank(message = "Telefone é obrigatório.")
    private String telefone;

    private Funcionario funcionario;

    public Telefone() {
        this.funcionario = new Funcionario();
    }

    public Telefone(Long funcionarioId) {
        this.funcionario = new Funcionario();
        funcionario.setId(funcionarioId);
    }

    public Telefone(Long funcionarioId, String telefone) {
        this.funcionario = new Funcionario();
        funcionario.setId(funcionarioId);
        this.telefone = telefone;
    }

    public TelefoneKey getKey() {
        return new TelefoneKey(this.funcionario.getId(), telefone);
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

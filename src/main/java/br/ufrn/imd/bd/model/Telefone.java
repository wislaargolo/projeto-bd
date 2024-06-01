package br.ufrn.imd.bd.model;

public class Telefone {

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

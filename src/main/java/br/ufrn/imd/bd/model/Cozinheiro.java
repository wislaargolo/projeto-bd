package br.ufrn.imd.bd.model;

public class Cozinheiro extends Funcionario {

    public Cozinheiro(Funcionario funcionario) {
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

    public Cozinheiro() {}
}

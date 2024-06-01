package br.ufrn.imd.bd.model;

import java.util.Objects;

public class TelefoneKey {

    private Long idFuncionario;
    private String telefoneFuncionario;

    public TelefoneKey(Long idFuncionario, String telefoneFuncionario) {
        this.idFuncionario = idFuncionario;
        this.telefoneFuncionario = telefoneFuncionario;
    }

    public Long getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(Long idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

    public String getTelefoneFuncionario() {
        return telefoneFuncionario;
    }

    public void setTelefoneFuncionario(String telefoneFuncionario) {
        this.telefoneFuncionario = telefoneFuncionario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TelefoneKey that = (TelefoneKey) o;
        return Objects.equals(idFuncionario, that.idFuncionario) &&
                Objects.equals(telefoneFuncionario, that.telefoneFuncionario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idFuncionario, telefoneFuncionario);
    }
}

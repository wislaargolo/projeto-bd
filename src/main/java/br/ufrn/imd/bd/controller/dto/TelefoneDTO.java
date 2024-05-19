package br.ufrn.imd.bd.controller.dto;

import br.ufrn.imd.bd.model.Telefone;
import jakarta.validation.constraints.NotNull;

public class TelefoneDTO {

    @NotNull
    private Telefone telefoneNovo;
    private Telefone telefoneHidden;

    public Telefone getTelefoneNovo() {
        return telefoneNovo;
    }

    public void setTelefoneNovo(Telefone telefoneNovo) {
        this.telefoneNovo = telefoneNovo;
    }

    public Telefone getTelefoneHidden() {
        return telefoneHidden;
    }

    public void setTelefoneHidden(Telefone telefoneHidden) {
        this.telefoneHidden = telefoneHidden;
    }
}

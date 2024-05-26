package br.ufrn.imd.bd.controller.dto;

import br.ufrn.imd.bd.model.InstanciaProduto;
import br.ufrn.imd.bd.model.Produto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class ProdutoDTO {

    private InstanciaProduto instanciaProduto;

    private Produto produto;

    public ProdutoDTO() {}

    public ProdutoDTO(InstanciaProduto instanciaProduto, Produto produto) {
        this.instanciaProduto = instanciaProduto;
        this.produto = produto;
    }

    public InstanciaProduto getInstanciaProduto() {
        return instanciaProduto;
    }

    public void setInstanciaProduto(InstanciaProduto instanciaProduto) {
        this.instanciaProduto = instanciaProduto;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }
}

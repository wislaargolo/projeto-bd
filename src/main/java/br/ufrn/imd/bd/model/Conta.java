package br.ufrn.imd.bd.model;

import br.ufrn.imd.bd.model.enums.MetodoPagamento;
import br.ufrn.imd.bd.model.enums.StatusConta;

import java.time.LocalDate;

public class Conta {

    private Long id;

    private Long idCaixa;

    private Long idAtendente;

    private Long idMesa;

    private StatusConta status;

    private MetodoPagamento metodoPagamento;

    private LocalDate dataHoraFinalizacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdCaixa() {
        return idCaixa;
    }

    public void setIdCaixa(Long idCaixa) {
        this.idCaixa = idCaixa;
    }

    public Long getIdAtendente() {
        return idAtendente;
    }

    public void setIdAtendente(Long idAtendente) {
        this.idAtendente = idAtendente;
    }

    public Long getIdMesa() {
        return idMesa;
    }

    public void setIdMesa(Long idMesa) {
        this.idMesa = idMesa;
    }

    public StatusConta getStatus() {
        return status;
    }

    public void setStatus(StatusConta status) {
        this.status = status;
    }

    public MetodoPagamento getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(MetodoPagamento metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public LocalDate getDataHoraFinalizacao() {
        return dataHoraFinalizacao;
    }

    public void setDataHoraFinalizacao(LocalDate dataHoraFinalizacao) {
        this.dataHoraFinalizacao = dataHoraFinalizacao;
    }
}

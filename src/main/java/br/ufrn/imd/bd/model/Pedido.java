package br.ufrn.imd.bd.model;

import br.ufrn.imd.bd.model.enums.StatusPedido;

import java.time.LocalDate;

public class Pedido {

    private Long id;

    private Long idAtendente;

    private Long idConta;

    private StatusPedido status;

    private LocalDate dataHoraRegistra;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdAtendente() {
        return idAtendente;
    }

    public void setIdAtendente(Long idAtendente) {
        this.idAtendente = idAtendente;
    }

    public Long getIdConta() {
        return idConta;
    }

    public void setIdConta(Long idConta) {
        this.idConta = idConta;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public LocalDate getDataHoraRegistra() {
        return dataHoraRegistra;
    }

    public void setDataHoraRegistra(LocalDate dataHoraRegistra) {
        this.dataHoraRegistra = dataHoraRegistra;
    }
}

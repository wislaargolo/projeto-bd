package br.ufrn.imd.bd.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/gerente/pedidos")
public class GerentePedidosController extends AtendentePedidosController{

    @Override
    public String getLayout() {
        return "gerente";
    }
}

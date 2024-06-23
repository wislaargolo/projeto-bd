package br.ufrn.imd.bd.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/garcom/pedidos")
public class GarcomController extends AtendentePedidosController {

    @Override
    public String getLayout() {
        return "garcom";
    }
}

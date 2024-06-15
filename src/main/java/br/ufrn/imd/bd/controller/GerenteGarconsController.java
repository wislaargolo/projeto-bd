package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.model.enums.TipoAtendente;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/gerente/garcons")
public class GerenteGarconsController extends AtendenteController {
    @Override
    public TipoAtendente getTipo() {
        return TipoAtendente.GARCOM;
    }

    @Override
    public String getUrl() {
        return "garcons";
    }
}

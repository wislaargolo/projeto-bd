package br.ufrn.imd.bd.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/gerente/atendentes")
public class GerenteAtendentesController {

    @GetMapping
    public String home() {
        return "mesa/lista";
    }
}

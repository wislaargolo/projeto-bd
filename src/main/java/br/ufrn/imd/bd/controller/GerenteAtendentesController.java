package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.model.Atendente;
import br.ufrn.imd.bd.model.enums.TipoAtendente;
import br.ufrn.imd.bd.service.AtendenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.SQLException;
import java.util.List;

@Controller
@RequestMapping("/gerente/atendentes")
public class GerenteAtendentesController {

    @Autowired
    private AtendenteService atendenteService;

    @GetMapping("/{tipo}")
    public String listarAtendentes(@PathVariable("tipo") String tipo, Model model) throws SQLException {
        List<Atendente> atendentes = atendenteService.buscarPorTipo(TipoAtendente.valueOf(tipo.toUpperCase()));
        model.addAttribute("atendentes", atendentes);
        model.addAttribute("tipo", tipo);
        return "atendente/lista";
    }

}

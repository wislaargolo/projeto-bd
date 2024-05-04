package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.model.Funcionario;
import br.ufrn.imd.bd.service.CaixaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/caixas")
public class CaixaController {

    @Autowired
    private CaixaService caixaService;

    @GetMapping
    public String listarTodosOsCaixas(Model model) {
        List<Funcionario> caixas = caixaService.buscarTodos();
        model.addAttribute("caixas", caixas);
        return "caixa/lista";
    }

    @GetMapping("/{id}")
    public String buscarCaixaPorId(@PathVariable Long id, Model model) {
        Funcionario caixa = caixaService.buscarPorId(id);
        model.addAttribute("caixa", caixa);
        return "caixa/info";
    }



}



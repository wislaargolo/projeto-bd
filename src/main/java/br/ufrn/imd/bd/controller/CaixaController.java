package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.model.Caixa;
import br.ufrn.imd.bd.model.Funcionario;
import br.ufrn.imd.bd.service.CaixaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/caixas")
public class CaixaController {

    @Autowired
    private CaixaService caixaService;

    @GetMapping
    public String listarTodosOsCaixas(Model model) {
        List<Caixa> caixas = caixaService.buscarTodos();
        model.addAttribute("caixas", caixas);
        return "caixa/lista";
    }

    @GetMapping("/novo")
    public String criarFormCaixa(Model model) {
        model.addAttribute("caixa", new Caixa());
        return "caixa/formulario";
    }

    @PostMapping
    public String salvarCaixa(@ModelAttribute Caixa caixa) {
        caixaService.salvar(caixa);
        return "redirect:/caixas";
    }



}



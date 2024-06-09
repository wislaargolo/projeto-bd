package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.model.Caixa;
import br.ufrn.imd.bd.service.CaixaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/gerente/caixas")
public class GerenteCaixasController {

    @Autowired
    private CaixaService caixaService;

    @GetMapping
    public String listarCaixas (Model model) throws SQLException {
        List<Caixa> caixas = caixaService.buscarTodos();
        model.addAttribute("caixas", caixas);
        return "caixa/lista";
    }

    @GetMapping("/novo")
    public String criarFormCaixa(Model model) {
        model.addAttribute("caixa", new Caixa());
        return "caixa/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editarFormCaixa(Model model, @PathVariable Long id) throws SQLException {
        model.addAttribute("caixa", caixaService.buscarPorId(id));
        return "caixa/formulario";
    }

    @PostMapping("/salvar")
    public String salvarCaixa(@ModelAttribute @Valid Caixa caixa, BindingResult bindingResult,
                              @RequestParam("confirmacaoSenha") String confirmacaoSenha, Model model) throws SQLException {
        List<String> errors = new ArrayList<>();

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        }

        if (caixa.getSenha() == null || caixa.getSenha().isEmpty()) {
            errors.add("Senha é obrigatória");
        }

        if (!caixa.getSenha().equals(confirmacaoSenha)) {
            errors.add("As senhas não coincidem");
        }

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return "caixa/formulario";
        }

        try {
            caixaService.salvar(caixa, confirmacaoSenha);
        } catch (EntidadeJaExisteException e) {
            errors.add(e.getMessage());
            model.addAttribute("errors", errors);
            return "caixa/formulario";
        }

        return "redirect:/gerente/caixas";
    }

    @PostMapping("/editar")
    public String editarCaixa(@ModelAttribute @Valid Caixa caixa, BindingResult bindingResult,
                              @RequestParam String confirmacaoSenha, Model model) throws SQLException {
        List<String> errors = new ArrayList<>();

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        }

        if (!caixa.getSenha().equals(confirmacaoSenha)) {
            errors.add("As senhas não coincidem");
        }

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return "caixa/formulario";
        }

        try {
            caixaService.atualizar(caixa, confirmacaoSenha);
        } catch (EntidadeJaExisteException e) {
            errors.add(e.getMessage());
            model.addAttribute("errors", errors);
            return "caixa/formulario";
        }

        return "redirect:/gerente/caixas";
    }


    @GetMapping("/{id}/excluir")
    public String excluirCaixa(@PathVariable Long id) throws SQLException {
        caixaService.deletarPorId(id);
        return "redirect:/gerente/caixas";
    }
}

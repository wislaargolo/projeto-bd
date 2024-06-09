package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.exceptions.SenhaInvalidaException;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.sql.SQLException;
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

    @PostMapping("salvar")
    public String salvarCaixa(@ModelAttribute @Valid Caixa caixa, BindingResult bindingResult,
                              @RequestParam("confirmacaoSenha") String confirmacaoSenha, Model model) throws SQLException {
        if (bindingResult.hasErrors()) {
            if (caixa.getSenha() == null || caixa.getSenha().isEmpty()) {
                model.addAttribute("error","Senha é obrigatória");
            }
            return "caixa/formulario";
        }

        try {
            caixaService.salvar(caixa, confirmacaoSenha);
        } catch (SenhaInvalidaException | EntidadeJaExisteException e) {
            model.addAttribute("error", e.getMessage());
            return "caixa/formulario";
        }
        return "redirect:/gerente/caixas";
    }

    @PostMapping("/editar")
    public String editarCaixa(@ModelAttribute @Valid Caixa caixa, BindingResult bindingResult,
                              @RequestParam String confirmacaoSenha, Model model) throws SQLException {
        if (bindingResult.hasErrors()) {
            return "caixa/formulario";
        }

        try {
            caixaService.atualizar(caixa, confirmacaoSenha);
        } catch (SenhaInvalidaException | EntidadeJaExisteException e) {
            model.addAttribute("error", e.getMessage());
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

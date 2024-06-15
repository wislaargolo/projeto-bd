package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.exceptions.EntidadeNaoExisteException;
import br.ufrn.imd.bd.model.Caixa;
import br.ufrn.imd.bd.model.Cozinheiro;
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
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/gerente/caixas")
public class GerenteCaixasController extends TelefoneController {

    @Autowired
    private CaixaService caixaService;

    @GetMapping
    public String listarCaixas (Model model) throws SQLException {
        List<Caixa> caixas = caixaService.buscarTodos();
        model.addAttribute("url", getUrl());
        model.addAttribute("funcionarios", caixas);
        return "funcionario/lista";
    }

    @GetMapping("/novo")
    public String criarFormCaixa(Model model) {
        model.addAttribute("url", getUrl());
        model.addAttribute("funcionario", new Caixa());
        return "funcionario/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editarFormCaixa(Model model, @PathVariable Long id, RedirectAttributes redirectAttributes) throws SQLException {
        model.addAttribute("url", getUrl());

        try {
            model.addAttribute("caixa", caixaService.buscarPorId(id));
        } catch (EntidadeNaoExisteException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/gerente/caixas";
        }
        return "funcionario/formulario";
    }

    @PostMapping("/salvar")
    public String salvarCaixa(@ModelAttribute("funcionario") @Valid Caixa caixa, BindingResult bindingResult,
                              @RequestParam("confirmacaoSenha") String confirmacaoSenha, Model model) throws SQLException {
        List<String> errors = new ArrayList<>();

        model.addAttribute("url", getUrl());

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
            return "funcionario/formulario";
        }

        try {
            caixaService.salvar(caixa);
        } catch (EntidadeJaExisteException e) {
            errors.add(e.getMessage());
            model.addAttribute("errors", errors);
            return "funcionario/formulario";
        }

        return "redirect:/gerente/caixas";
    }

    @PostMapping("/editar")
    public String editarCaixa(@ModelAttribute("funcionario") @Valid Caixa caixa, BindingResult bindingResult,
                              @RequestParam String confirmacaoSenha, Model model, RedirectAttributes redirectAttributes) throws SQLException {
        List<String> errors = new ArrayList<>();
        model.addAttribute("url", getUrl());

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        }

        if (!caixa.getSenha().equals(confirmacaoSenha)) {
            errors.add("As senhas não coincidem");
        }

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return "funcionario/formulario";
        }

        try {
            caixaService.atualizar(caixa);
        } catch (EntidadeJaExisteException e) {
            errors.add(e.getMessage());
            model.addAttribute("errors", errors);
            return "funcionario/formulario";
        } catch (EntidadeNaoExisteException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/gerente/caixas";
        }

        return "redirect:/gerente/caixas";
    }


    @GetMapping("/{id}/excluir")
    public String excluirCaixa(@PathVariable Long id, RedirectAttributes redirectAttributes) throws SQLException {
        try {
            caixaService.deletar(id);
        } catch (EntidadeNaoExisteException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/gerente/caixas";
        }
        return "redirect:/gerente/caixas";
    }

    @Override
    public String getUrl() {
        return "caixas";
    }
}

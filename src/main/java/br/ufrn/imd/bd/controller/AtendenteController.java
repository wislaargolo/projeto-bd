package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.exceptions.EntidadeNaoExisteException;
import br.ufrn.imd.bd.model.Atendente;
import br.ufrn.imd.bd.model.Caixa;
import br.ufrn.imd.bd.model.enums.TipoAtendente;
import br.ufrn.imd.bd.service.AtendenteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public abstract class AtendenteController extends TelefoneController {

    @Autowired
    private AtendenteService atendenteService;

    public abstract TipoAtendente getTipo();

    @GetMapping
    public String listarAtendentes(Model model) throws SQLException {
        List<Atendente> atendentes = atendenteService.buscarPorTipo(getTipo());
        model.addAttribute("url", getUrl());
        model.addAttribute("funcionarios", atendentes);
        return "funcionario/lista";
    }
    @GetMapping("/novo")
    public String criarFormAtendente(Model model) {
        model.addAttribute("url", getUrl());
        model.addAttribute("funcionario", new Caixa());
        return "funcionario/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editarFormAtendente(@PathVariable Long id,
                                      RedirectAttributes redirectAttributes, Model model) throws SQLException {
        model.addAttribute("url", getUrl());

        try {
            model.addAttribute("funcionario", atendenteService.buscarPorId(id));
        } catch (EntidadeNaoExisteException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/gerente/" + getUrl();
        }
        return "funcionario/formulario";
    }

    @PostMapping("/salvar")
    public String salvarAtendente(@ModelAttribute("funcionario") @Valid Atendente atendente, BindingResult bindingResult,
                                  @RequestParam("confirmacaoSenha") String confirmacaoSenha, Model model) throws SQLException {
        List<String> errors = new ArrayList<>();
        model.addAttribute("url", getUrl());

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        }

        if (atendente.getSenha() == null || atendente.getSenha().isEmpty()) {
            errors.add("Senha é obrigatória.");
        }

        if (!atendente.getSenha().equals(confirmacaoSenha)) {
            errors.add("As senhas não coincidem!");
        }

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return "funcionario/formulario";
        }

        try {
            atendente.setTipo(getTipo());
            atendenteService.salvar(atendente);
        } catch (EntidadeJaExisteException e) {
            errors.add(e.getMessage());
            model.addAttribute("errors", errors);
            return "funcionario/formulario";
        }
        return "redirect:/gerente/" + getUrl();
    }

    @PostMapping("/editar")
    public String editarAtendente(@ModelAttribute("funcionario") @Valid Atendente atendente, BindingResult bindingResult,
                                  @RequestParam String confirmacaoSenha, Model model, RedirectAttributes redirectAttributes) throws SQLException {
        List<String> errors = new ArrayList<>();
        model.addAttribute("url", getUrl());

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        }

        if (!atendente.getSenha().equals(confirmacaoSenha)) {
            errors.add("As senhas não coincidem");
        }

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return "funcionario/formulario";
        }

        try {
            atendente.setTipo(getTipo());
            atendenteService.atualizar(atendente);
        } catch (EntidadeJaExisteException e) {
            errors.add(e.getMessage());
            model.addAttribute("errors", errors);
            return "funcionario/formulario";
        } catch (EntidadeNaoExisteException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/gerente/" + getUrl();
        }
        return "redirect:/gerente/" + getUrl();
    }

    @GetMapping("/{id}/excluir")
    public String excluirAtendente(@PathVariable Long id, RedirectAttributes redirectAttributes) throws SQLException {
        try {
            atendenteService.deletar(id);
        } catch (EntidadeNaoExisteException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/gerente/" + getUrl();
        }
        return "redirect:/gerente/" + getUrl();
    }
}
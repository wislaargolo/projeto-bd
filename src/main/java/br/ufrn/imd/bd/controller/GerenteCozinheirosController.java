package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.exceptions.EntidadeNaoExisteException;
import br.ufrn.imd.bd.model.Cozinheiro;
import br.ufrn.imd.bd.service.CozinheiroService;
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
@RequestMapping("/gerente/cozinheiros")
public class GerenteCozinheirosController extends TelefoneController {

    @Autowired
    private CozinheiroService cozinheiroService;

    @GetMapping
    public String listarCozinheiros(Model model) throws SQLException {
        List<Cozinheiro> cozinheiros = cozinheiroService.buscarTodos();
        model.addAttribute("url", getUrl());
        model.addAttribute("funcionarios", cozinheiros);
        return "funcionario/lista";
    }

    @GetMapping("/novo")
    public String criarFormCozinheiro(Model model) {
        model.addAttribute("url", getUrl());
        model.addAttribute("funcionario", new Cozinheiro());
        return "funcionario/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editarFormCozinheiro(Model model, @PathVariable Long id, RedirectAttributes redirectAttributes) throws SQLException {
        model.addAttribute("url", getUrl());
        try {
            model.addAttribute("funcionario", cozinheiroService.buscarPorId(id));
        } catch (EntidadeNaoExisteException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/gerente/cozinheiros";
        }
        return "funcionario/formulario";
    }

    @PostMapping("/salvar")
    public String salvarCozinheiro(@ModelAttribute("funcionario") @Valid Cozinheiro funcionario, BindingResult bindingResult,
                                   @RequestParam("confirmacaoSenha") String confirmacaoSenha, Model model) throws SQLException {
        List<String> errors = new ArrayList<>();

        model.addAttribute("url", getUrl());

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        }

        if (funcionario.getSenha() == null || funcionario.getSenha().isEmpty()) {
            errors.add("Senha é obrigatória");
        }

        if (!funcionario.getSenha().equals(confirmacaoSenha)) {
            errors.add("As senhas não coincidem");
        }

        if (funcionario.getSenha().length() < 6 || funcionario.getSenha().length() > 15 ) {
            errors.add("A senha deve ter entre 6 e 15 caracteres.");
        }

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return "funcionario/formulario";
        }

        try {
            cozinheiroService.salvar(funcionario);
        } catch (EntidadeJaExisteException e) {
            errors.add(e.getMessage());
            model.addAttribute("errors", errors);
            return "funcionario/formulario";
        }

        return "redirect:/gerente/cozinheiros";
    }

    @PostMapping("/editar")
    public String editarCozinheiro(@ModelAttribute("funcionario") @Valid Cozinheiro funcionario, BindingResult bindingResult,
                                   @RequestParam String confirmacaoSenha, Model model, RedirectAttributes redirectAttributes) throws SQLException {
        List<String> errors = new ArrayList<>();

        model.addAttribute("url", getUrl());

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        }

        if(!funcionario.getSenha().isEmpty()) {
            if (!funcionario.getSenha().equals(confirmacaoSenha)) {
                errors.add("As senhas não coincidem");
            } else if (funcionario.getSenha().length() < 6 || funcionario.getSenha().length() > 15) {
                errors.add("A senha deve ter entre 6 e 15 caracteres.");
            }
        }

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return "funcionario/formulario";
        }

        try {
            cozinheiroService.atualizar(funcionario);
        } catch (EntidadeJaExisteException e) {
            errors.add(e.getMessage());
            model.addAttribute("errors", errors);
            return "funcionario/formulario";
        } catch (EntidadeNaoExisteException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/gerente/cozinheiros";
        }

        return "redirect:/gerente/cozinheiros";
    }

    @PostMapping("/{id}/excluir")
    public String excluirCozinheiro(@PathVariable Long id, RedirectAttributes redirectAttributes) throws SQLException {
        try {
            cozinheiroService.deletar(id);
        } catch (EntidadeNaoExisteException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/gerente/cozinheiros";
        }
        return "redirect:/gerente/cozinheiros";
    }
    @Override
    public String getUrl() {
        return "cozinheiros";
    }
}
package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/gerente/cozinheiros")
public class GerenteCozinheirosController {

    @Autowired
    private CozinheiroService cozinheiroService;

    @GetMapping
    public String listarCozinheiros(Model model) throws SQLException {
        List<Cozinheiro> cozinheiros = cozinheiroService.buscarTodos();
        model.addAttribute("cozinheiros", cozinheiros);
        return "cozinheiro/lista";
    }

    @GetMapping("/novo")
    public String criarFormCozinheiro(Model model) {
        model.addAttribute("cozinheiro", new Cozinheiro());
        return "cozinheiro/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editarFormCozinheiro(Model model, @PathVariable Long id) throws SQLException {
        model.addAttribute("cozinheiro", cozinheiroService.buscarPorId(id));
        return "cozinheiro/formulario";
    }

    @PostMapping("/salvar")
    public String salvarCozinheiro(@ModelAttribute @Valid Cozinheiro cozinheiro, BindingResult bindingResult,
                              @RequestParam("confirmacaoSenha") String confirmacaoSenha, Model model) throws SQLException {
        List<String> errors = new ArrayList<>();

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        }

        if (cozinheiro.getSenha() == null || cozinheiro.getSenha().isEmpty()) {
            errors.add("Senha é obrigatória");
        }

        if (!cozinheiro.getSenha().equals(confirmacaoSenha)) {
            errors.add("As senhas não coincidem");
        }

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return "cozinheiro/formulario";
        }

        try {
            cozinheiroService.salvar(cozinheiro);
        } catch (EntidadeJaExisteException e) {
            errors.add(e.getMessage());
            model.addAttribute("errors", errors);
            return "cozinheiro/formulario";
        }

        return "redirect:/gerente/cozinheiros";
    }

    @PostMapping("/editar")
    public String editarCozinheiro(@ModelAttribute @Valid Cozinheiro cozinheiro, BindingResult bindingResult,
                              @RequestParam String confirmacaoSenha, Model model) throws SQLException {
        List<String> errors = new ArrayList<>();

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        }

        if (!cozinheiro.getSenha().equals(confirmacaoSenha)) {
            errors.add("As senhas não coincidem");
        }

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return "cozinheiro/formulario";
        }

        try {
            cozinheiroService.atualizar(cozinheiro);
        } catch (EntidadeJaExisteException e) {
            errors.add(e.getMessage());
            model.addAttribute("errors", errors);
            return "cozinheiro/formulario";
        }

        return "redirect:/gerente/cozinheiros";
    }

    @GetMapping("/{id}/excluir")
    public String excluirCozinheiro(@PathVariable Long id) throws SQLException {
        cozinheiroService.deletar(id);
        return "redirect:/cozinheiros";
    }
}

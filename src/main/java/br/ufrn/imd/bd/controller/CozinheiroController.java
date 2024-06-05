package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.model.Cozinheiro;
import br.ufrn.imd.bd.service.CozinheiroService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@Controller
@RequestMapping("/cozinheiros")
public class CozinheiroController {

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

    @GetMapping("/editar/{id}")
    public String editarFormCozinheiro(Model model, @PathVariable Long id) throws SQLException {
        model.addAttribute("cozinheiro", cozinheiroService.buscarPorId(id));
        return "cozinheiro/formulario";
    }

    @PostMapping
    public String salvarCozinheiro(@ModelAttribute @Valid Cozinheiro cozinheiro, BindingResult bindingResult) throws SQLException, EntidadeJaExisteException {
        if (bindingResult.hasErrors()) {
            return "cozinheiro/formulario";
        }
        cozinheiroService.salvar(cozinheiro);
        return "redirect:/cozinheiros";
    }

    @PostMapping("/editar")
    public String editarCozinheiro(@ModelAttribute @Valid Cozinheiro cozinheiro, BindingResult bindingResult) throws SQLException, EntidadeJaExisteException {
        if (bindingResult.hasErrors()) {
            return "cozinheiro/formulario";
        }
        cozinheiroService.atualizar(cozinheiro);
        return "redirect:/cozinheiros";
    }

    @GetMapping("/excluir/{id}")
    public String excluirCozinheiro(@PathVariable Long id) throws SQLException {
        cozinheiroService.deletarPorId(id);
        return "redirect:/cozinheiros";
    }
}

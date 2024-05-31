package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.model.Atendente;
import br.ufrn.imd.bd.service.AtendenteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@Controller
@RequestMapping("/atendentes")
public class AtendenteController {

    @Autowired
    private AtendenteService atendenteService;

    @GetMapping
    public String listarTodosOsAtendentes(Model model) throws SQLException {
        List<Atendente> atendentes = atendenteService.buscarTodos();
        model.addAttribute("atendentes", atendentes);
        return "atendente/lista";
    }

    @GetMapping("/novo")
    public String criarFormAtendente(Model model) {
        model.addAttribute("atendente", new Atendente());
        return "atendente/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editarFormAtendente(Model model, @PathVariable Long id) throws SQLException {
        model.addAttribute("atendente", atendenteService.buscarPorId(id));
        return "atendente/formulario";
    }

    @PostMapping
    public String salvarAtendente(@ModelAttribute @Valid Atendente atendente, BindingResult bindingResult) throws SQLException, EntidadeJaExisteException {
        if (bindingResult.hasErrors()) {
            return "atendente/formulario";
        }
        atendenteService.salvar(atendente);
        return "redirect:/atendentes";
    }

    @PostMapping("/editar")
    public String editarAtendente(@ModelAttribute @Valid Atendente atendente, BindingResult bindingResult) throws SQLException, EntidadeJaExisteException {
        if (bindingResult.hasErrors()) {
            return "atendente/formulario";
        }
        atendenteService.atualizar(atendente);
        return "redirect:/atendentes";
    }

    @GetMapping("/excluir/{id}")
    public String excluirAtendente(@PathVariable Long id) throws SQLException {
        atendenteService.deletarPorId(id);
        return "redirect:/atendentes";
    }
}
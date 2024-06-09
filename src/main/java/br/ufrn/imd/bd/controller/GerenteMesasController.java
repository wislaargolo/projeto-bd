package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.model.Mesa;
import br.ufrn.imd.bd.service.MesaService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;
import java.util.List;

@Controller
@RequestMapping("/gerente/mesas")
public class GerenteMesasController {

    @Autowired
    private MesaService mesaService;

    @GetMapping
    public String listarMesas(Model model) throws SQLException {
        List<Mesa> mesas = mesaService.buscarTodos();
        model.addAttribute("mesas", mesas);
        return "mesa/lista";
    }

    @GetMapping("/nova")
    public String criarFormMesa(Model model) {
        model.addAttribute("mesa", new Mesa());
        return "mesa/formulario";
    }

    @GetMapping("{id}/editar")
    public String editarFormMesa(Model model, @PathVariable Long id) throws SQLException {
        model.addAttribute("mesa", mesaService.buscarPorId(id));
        return "mesa/formulario";
    }

    @PostMapping("/salvar")
    public String salvarMesa(@ModelAttribute @Valid Mesa mesa, BindingResult bindingResult, Model model) throws SQLException {
        if (bindingResult.hasErrors()) {
            return "mesa/formulario";
        }

        try {
            mesaService.salvar(mesa);
        } catch (EntidadeJaExisteException e) {
            model.addAttribute("error", e.getMessage());
            return "mesa/formulario";
        }

        return "redirect:/gerente/mesas";
    }

    @PostMapping("/editar")
    public String editarMesa(@ModelAttribute @Valid Mesa mesa, BindingResult bindingResult, Model model) throws SQLException {
        if (bindingResult.hasErrors()) {
            return "mesa/formulario";
        }

        try {
            mesaService.atualizar(mesa);
        } catch (EntidadeJaExisteException e) {
            model.addAttribute("error", e.getMessage());
            return "mesa/formulario";
        }

        return "redirect:/gerente/mesas";
    }

    @GetMapping("{id}/excluir")
    public String excluirMesa(@PathVariable Long id) throws SQLException {
        mesaService.deletarPorId(id);
        return "redirect:/gerente/mesas";
    }

}

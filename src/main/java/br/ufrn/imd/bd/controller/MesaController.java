package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.model.Mesa;
import br.ufrn.imd.bd.service.MesaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@Controller
@RequestMapping("/mesas")
public class MesaController {

    @Autowired
    private MesaService mesaService;

    @GetMapping
    public String listarTodosOsCaixas(Model model) throws SQLException {
        List<Mesa> mesas = mesaService.buscarTodos();
        model.addAttribute("mesas", mesas);
        return "mesa/lista";
    }

    @GetMapping("/novo")
    public String criarFormMesa(Model model) {
        model.addAttribute("mesa", new Mesa());
        return "mesa/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editarFormmesa(Model model, @PathVariable Long id) throws SQLException {
        model.addAttribute("mesa", mesaService.buscarPorId(id));
        return "mesa/formulario";
    }

    @PostMapping
    public String salvarMesa(@ModelAttribute @Valid Mesa mesa, BindingResult bindingResult) throws SQLException, EntidadeJaExisteException {
        if (bindingResult.hasErrors()) {
            return "mesa/formulario";
        }
        if(mesa.getId() == null) {
            mesaService.salvar(mesa);
        } else {
            mesaService.atualizar(mesa);
        }
        return "redirect:/mesas";
    }

    @GetMapping("/excluir/{id}")
    public String excluirMesa(@PathVariable Long id) throws SQLException {
        mesaService.deletarPorId(id);
        return "redirect:/mesas";
    }
}

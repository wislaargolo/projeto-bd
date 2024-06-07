package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.model.Conta;
import br.ufrn.imd.bd.model.Cozinheiro;
import br.ufrn.imd.bd.model.Mesa;
import br.ufrn.imd.bd.service.ContaService;
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

import java.sql.SQLException;
import java.util.List;

@Controller
@RequestMapping("/contas")
public class ContaController {

    @Autowired
    private ContaService contaService;

    @Autowired
    private MesaService mesaService;

    @GetMapping
    public String listarContas(Model model) throws SQLException {

        List<Conta> contas = contaService.buscarTodos();
        model.addAttribute("contas", contas);
        return "conta/lista";
    }

    @GetMapping("/cancelar/{id}")
    public String cancelarConta(@PathVariable Long id) throws SQLException {
        contaService.deletar(id);
        return "redirect:/contas";
    }

    @GetMapping("/editar/{id}")
    public String editarFormConta(Model model, @PathVariable Long id) throws SQLException {
        model.addAttribute("conta", contaService.buscarPorId(id));
        model.addAttribute("mesas", mesaService.buscarTodos());
        return "conta/formulario";
    }

    @PostMapping("/editar")
    public String editarConta(@ModelAttribute @Valid Conta conta, BindingResult bindingResult) throws SQLException {
        if (bindingResult.hasErrors()) {
            return "conta/formulario";
        }
        contaService.atualizar(conta);
        return "redirect:/contas";
    }
}

package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.exceptions.EntidadeNaoExisteException;
import br.ufrn.imd.bd.model.Conta;
import br.ufrn.imd.bd.model.enums.MetodoPagamento;
import br.ufrn.imd.bd.service.ContaService;
import br.ufrn.imd.bd.service.MesaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static br.ufrn.imd.bd.model.enums.StatusConta.FINALIZADA;

@Controller
@RequestMapping("/caixa/contas")
public class CaixaContasController {

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


    @GetMapping("/{id}/editar")
    public String editarFormConta(Model model, @PathVariable Long id, RedirectAttributes redirectAttributes) throws SQLException {
        try {
            model.addAttribute("conta", contaService.buscarPorId(id));
        } catch (EntidadeNaoExisteException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/caixa/contas";
        }
        return "conta/formulario";
    }

    @PostMapping("/editar")
    public String editarConta(@ModelAttribute @Valid Conta conta, BindingResult bindingResult,
                              Model model, RedirectAttributes redirectAttributes) throws SQLException {
        if (bindingResult.hasErrors()) {
            return "conta/formulario";
        }
        try {
            contaService.atualizar(conta);
        } catch (EntidadeNaoExisteException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/caixa/contas";
        }
        return "redirect:/caixa/contas";
    }


    @GetMapping("/relatorio")
    public String relatorioGanhoPorMetodoPagamento(Model model) throws SQLException {
        Map<String, Double> totalGanhoPorMetodo = contaService.getTotalGanhoPorMetodoPagamento();
        model.addAttribute("totalGanhoPorMetodo", totalGanhoPorMetodo);
        return "conta/relatorio";
    }
}

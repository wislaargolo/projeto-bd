package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.exceptions.EntidadeNaoExisteException;
import br.ufrn.imd.bd.model.Conta;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;
import java.util.List;

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

    @GetMapping("/cancelar/{id}")
    public String cancelarConta(@PathVariable Long id) throws SQLException {
        contaService.deletar(id);
        return "redirect:/contas";
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
    public String editarConta(@ModelAttribute @Valid Conta conta, BindingResult bindingResult) throws SQLException {
        if (bindingResult.hasErrors()) {
            return "conta/formulario";
        }
        try {
            conta.setStatusConta(FINALIZADA);
            contaService.atualizar(conta);
        } catch (EntidadeNaoExisteException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/caixa/contas";
    }
}

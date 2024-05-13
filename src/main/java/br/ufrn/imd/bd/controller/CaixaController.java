package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.exceptions.FuncionarioJaExisteException;
import br.ufrn.imd.bd.model.Caixa;
import br.ufrn.imd.bd.service.CaixaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@Controller
@RequestMapping("/caixas")
public class CaixaController {

    @Autowired
    private CaixaService caixaService;

    @GetMapping
    public String listarTodosOsCaixas(Model model) throws SQLException {
        List<Caixa> caixas = caixaService.buscarTodos();
        model.addAttribute("caixas", caixas);
        return "caixa/lista";
    }

    @GetMapping("/novo")
    public String criarFormCaixa(Model model) {
        model.addAttribute("caixa", new Caixa());
        return "caixa/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editarFormCaixa(Model model, @PathVariable Long id) throws SQLException {
        model.addAttribute("caixa", caixaService.buscarPorId(id));
        return "caixa/formulario";
    }

    @PostMapping
    public String salvarCaixa(@ModelAttribute @Valid Caixa caixa, BindingResult bindingResult) throws SQLException, FuncionarioJaExisteException {
        if (bindingResult.hasErrors()) {
            return "caixa/formulario";
        }
        if(caixa.getId() == null) {
            caixaService.salvar(caixa);
        } else {
            caixaService.atualizar(caixa);
        }
        return "redirect:/caixas";
    }

    @GetMapping("/excluir/{id}")
    public String excluirCaixa(@PathVariable Long id) throws SQLException {
        caixaService.deletarPorId(id);
        return "redirect:/caixas";
    }
}
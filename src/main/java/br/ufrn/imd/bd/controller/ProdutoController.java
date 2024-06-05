package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.model.Caixa;
import br.ufrn.imd.bd.model.InstanciaProduto;
import br.ufrn.imd.bd.model.Telefone;
import br.ufrn.imd.bd.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
//esse controller vai sumir
@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @GetMapping
    public String listarProdutos (Model model) throws SQLException {
        model.addAttribute("instanciaProdutoList", produtoService.buscarTodos());
        return "produto/lista";
    }

    @GetMapping("/novo")
    public String criarFormProduto(Model model) {
        model.addAttribute("instanciaProduto", new InstanciaProduto());
        return "produto/formulario";
    }

    @PostMapping
    public String salvarProduto(@ModelAttribute @Valid InstanciaProduto instanciaProduto, BindingResult bindingResult) throws SQLException, EntidadeJaExisteException {
        if (bindingResult.hasErrors()) {
            return "produto/formulario";
        }
        produtoService.salvar(instanciaProduto);
        return "redirect:/produtos";
    }

    @GetMapping("/{id}/editar")
    public String editarProduto(Model model, @PathVariable Long id) throws SQLException {
        model.addAttribute("instanciaProduto", produtoService.buscarPorId(id));
        return "produto/formulario";
    }

    @GetMapping("/{id}/excluir")
    public String excluirProduto(@PathVariable Long id) throws SQLException {
        produtoService.deletar(id);
        return "redirect:/produtos";
    }
}

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
public abstract class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    public abstract String getLayout();

    @GetMapping("/produtos")
    public String listarProdutos (Model model) throws SQLException {
        model.addAttribute("layout", getLayout() + "/layout");
        model.addAttribute("instanciaProdutoList", produtoService.buscarTodos());
        return "produto/lista";
    }

    @GetMapping("/produtos/novo")
    public String criarFormProduto(Model model) {
        model.addAttribute("layout", getLayout() + "/layout");
        model.addAttribute("instanciaProduto", new InstanciaProduto());
        return "produto/formulario";
    }

    @PostMapping("/produtos/salvar")
    public String salvarProduto(@ModelAttribute @Valid InstanciaProduto instanciaProduto,
                                BindingResult bindingResult, Model model) throws SQLException {
        if (bindingResult.hasErrors()) {
            model.addAttribute("layout", getLayout() + "/layout");
            return "produto/formulario";
        }
        try {
            produtoService.salvar(instanciaProduto);
        } catch (EntidadeJaExisteException e) {
            bindingResult.rejectValue("produto.nome", "error.produto", e.getMessage());
            model.addAttribute("layout", getLayout() + "/layout");
            model.addAttribute("instanciaProduto", instanciaProduto);
            return "produto/formulario";
        }
        return "redirect:/" + getLayout() + "/produtos";
    }

    @GetMapping("produtos/{id}/editar")
    public String editarProduto(Model model, @PathVariable Long id) throws SQLException {
        model.addAttribute("layout", getLayout() + "/layout");
        model.addAttribute("instanciaProduto", produtoService.buscarPorId(id));
        return "produto/formulario";
    }

    @GetMapping("produtos/{id}/excluir")
    public String excluirProduto(@PathVariable Long id) throws SQLException {
        produtoService.deletar(id);
        return "redirect:/" + getLayout() + "/produtos";
    }
}


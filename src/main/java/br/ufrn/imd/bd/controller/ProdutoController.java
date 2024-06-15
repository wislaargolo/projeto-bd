package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.exceptions.EntidadeNaoExisteException;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;
import java.util.List;
public abstract class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    public abstract String getLayout();

    @GetMapping
    public String listarProdutos (Model model) throws SQLException {
        model.addAttribute("layout", getLayout() + "/layout");
        model.addAttribute("instanciaProdutoList", produtoService.buscarTodos());
        return "produto/lista";
    }

    @GetMapping("/novo")
    public String criarFormProduto(Model model) {
        model.addAttribute("layout", getLayout() + "/layout");
        model.addAttribute("instanciaProduto", new InstanciaProduto());
        return "produto/formulario";
    }

    @PostMapping("/salvar")
    public String salvarProduto(@ModelAttribute @Valid InstanciaProduto instanciaProduto,
                                BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) throws SQLException {
        model.addAttribute("layout", getLayout() + "/layout");

        if (bindingResult.hasErrors()) {
            return "produto/formulario";
        }

        try {
            produtoService.salvar(instanciaProduto);
        } catch (EntidadeJaExisteException e) {
            model.addAttribute("error", e.getMessage());
            return "produto/formulario";

        } catch (EntidadeNaoExisteException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/" + getLayout() + "/produtos";
        }
        return "redirect:/" + getLayout() + "/produtos";
    }

    @GetMapping("/{id}/editar")
    public String editarProduto(Model model, @PathVariable Long id, RedirectAttributes redirectAttributes) throws SQLException {
        try {
            model.addAttribute("layout", getLayout() + "/layout");
            model.addAttribute("instanciaProduto", produtoService.buscarPorId(id));
        } catch (EntidadeNaoExisteException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/" + getLayout() + "/produtos";
        }
        return "produto/formulario";
    }

    @GetMapping("/{id}/excluir")
    public String excluirProduto(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) throws SQLException {

        try {
            produtoService.deletar(id);
        } catch (EntidadeNaoExisteException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/" + getLayout() + "/produtos";
        }
        return "redirect:/" + getLayout() + "/produtos";
    }
}

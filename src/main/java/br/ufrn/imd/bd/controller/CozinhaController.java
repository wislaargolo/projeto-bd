package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.model.Cozinheiro;
import br.ufrn.imd.bd.model.InstanciaProduto;
import br.ufrn.imd.bd.service.ProdutoService;
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
@RequestMapping("/cozinha")
public class CozinhaController {

    @GetMapping
    public String home() {
        return "redirect:/pedidos";
    }

    @Autowired
    private ProdutoService produtoService;

    @GetMapping("/produtos")
    public String listarProdutos (Model model) throws SQLException {
        model.addAttribute("layout", "cozinha/layout");
        model.addAttribute("instanciaProdutoList", produtoService.buscarTodos());
        return "produto/lista";
    }

    @GetMapping("/produtos/novo")
    public String criarFormProduto(Model model) {
        model.addAttribute("layout", "cozinha/layout");
        model.addAttribute("instanciaProduto", new InstanciaProduto());
        return "produto/formulario";
    }

    @PostMapping("/produtos/salvar")
    public String salvarProduto(@ModelAttribute @Valid InstanciaProduto instanciaProduto,
                                BindingResult bindingResult, Model model) throws SQLException, EntidadeJaExisteException {
        if (bindingResult.hasErrors()) {
            model.addAttribute("layout", "cozinha/layout");
            return "produto/formulario";
        }
        produtoService.salvar(instanciaProduto);
        return "redirect:/cozinha/produtos";
    }

    @GetMapping("produtos/{id}/editar")
    public String editarProduto(Model model, @PathVariable Long id) throws SQLException {
        model.addAttribute("layout", "cozinha/layout");
        model.addAttribute("instanciaProduto", produtoService.buscarPorId(id));
        return "produto/formulario";
    }

    @GetMapping("produtos/{id}/excluir")
    public String excluirProduto(@PathVariable Long id) throws SQLException {
        produtoService.deletar(id);
        return "redirect:/cozinha/produtos";
    }
}




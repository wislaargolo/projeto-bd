package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.controller.dto.ProdutoDTO;
import br.ufrn.imd.bd.controller.dto.TelefoneDTO;
import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.model.InstanciaProduto;
import br.ufrn.imd.bd.model.Produto;
import br.ufrn.imd.bd.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @GetMapping
    public String listarTodosProdutos(Model model) throws SQLException {;
        model.addAttribute("produtos", produtoService.buscarTodos());
        return "produto/lista";
    }

    @GetMapping("/novo")
    public String criarFormProduto(Model model) throws SQLException {
        model.addAttribute("produtoDTO", new ProdutoDTO(new InstanciaProduto(), new Produto()));
        return "produto/formulario";
    }

    @PostMapping
    public String salvarProduto(@ModelAttribute @Valid ProdutoDTO produtoDTO, BindingResult bindingResult) throws SQLException, EntidadeJaExisteException {
        if (bindingResult.hasErrors()) {
            return "produto/formulario";
        }
        produtoService.salvar(produtoDTO.getInstanciaProduto(), produtoDTO.getProduto());
        return "redirect:/produtos";
    }

    @GetMapping("/editar/{id}")
    public String editarFormmesa(Model model, @PathVariable Long id) throws SQLException {
        model.addAttribute("produtoDTO", produtoService.buscarPorId(id));
        return "produto/formulario";
    }

    @GetMapping("/excluir/{id}")
    public String excluirProduto(@PathVariable Long id) throws SQLException {
        produtoService.inativarProduto(id);
        return "redirect:/produtos";
    }
}

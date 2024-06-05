package br.ufrn.imd.bd.controller;

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

import java.sql.SQLException;
import java.util.List;

@Controller
@RequestMapping("/cozinha")
public class CozinhaController {

    @GetMapping
    public String home() throws SQLException {
        return "redirect:/pedidos";
    }

    @GetMapping("/produtos")
    public String listarProdutos(Model model) {
        return "forward:/produtos?layout=cozinha/layout";
    }

    @GetMapping("produtos/novo")
    public String criarProdutoCozinha(Model model) {
        return "forward:/produtos/novo?layout=cozinha/layout";
    }

    @PostMapping("produtos/salvar")
    public String salvarProdutoCozinha(@ModelAttribute @Valid InstanciaProduto instanciaProduto,
                                       BindingResult bindingResult, Model model) throws SQLException {
        return "forward:/produtos/salvar?layout=cozinha/layout";
    }

    @GetMapping("produtos/{id}/editar")
    public String editarProdutoCozinha(@PathVariable Long id, Model model) throws SQLException {
        return "forward:/produtos/" + id + "/editar?layout=cozinha/layout";
    }

    @GetMapping("/produtos/{id}/excluir")
    public String excluirProdutoCozinha(@PathVariable Long id, Model model) {
        return "forward:/produtos/" + id + "/excluir?layout=cozinha/layout";
    }
}



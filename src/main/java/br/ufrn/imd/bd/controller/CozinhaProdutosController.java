package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.model.InstanciaProduto;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.SQLException;

@Controller
@RequestMapping("/cozinha/produtos")
public class CozinhaProdutosController {


    @GetMapping
    public String listarProdutos(Model model) {
        return "forward:/produtos?layout=cozinha/layout";
    }

    @GetMapping("/novo")
    public String criarForm(Model model) {
        return "forward:/produtos/novo?layout=cozinha/layout";
    }

    @PostMapping("/salvar")
    public String salvarProduto(@ModelAttribute @Valid InstanciaProduto instanciaProduto,
                                       BindingResult bindingResult, Model model) throws SQLException {
        return "forward:/produtos/salvar?layout=cozinha/layout";
    }

    @GetMapping("/{id}/editar")
    public String editarProduto(@PathVariable Long id, Model model) throws SQLException {
        return "forward:/produtos/" + id + "/editar?layout=cozinha/layout";
    }

    @GetMapping("/{id}/excluir")
    public String excluirProduto(@PathVariable Long id, Model model) {
        return "forward:/produtos/" + id + "/excluir?layout=cozinha/layout";
    }
}



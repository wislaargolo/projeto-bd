package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.model.Cozinheiro;
import br.ufrn.imd.bd.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.SQLException;
import java.util.List;

@Controller
@RequestMapping("/cozinha")
public class CozinhaController extends ProdutoController {

    @GetMapping
    public String home() throws SQLException {
        return "redirect:/pedidos";
    }


    @Override
    public String getLayout() {
        return "cozinha";
    }
}

package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.model.Cozinheiro;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.SQLException;
import java.util.List;

@Controller
@RequestMapping("/")
public class CozinhaController {

    @GetMapping
    public String principal(Model model) throws SQLException {
        return "pedido/lista";
    }
}

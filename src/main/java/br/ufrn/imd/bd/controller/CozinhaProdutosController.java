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
public class CozinhaProdutosController extends ProdutoController{

    @Override
    public String getLayout() {
        return "cozinha";
    }

}



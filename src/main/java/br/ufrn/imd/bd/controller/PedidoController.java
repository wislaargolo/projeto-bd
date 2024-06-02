package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.service.PedidoService;
import br.ufrn.imd.bd.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.SQLException;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    public String listarPedidos(Model model) throws SQLException {
        model.addAttribute("pedidos", pedidoService.buscarTodos());
        return "pedido/lista";
    }
}

package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.service.MesaService;
import br.ufrn.imd.bd.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.SQLException;

public abstract class AtendentePedidosController {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private MesaService mesaService;

    public abstract String getLayout();

    @GetMapping("/mesas")
    public String listarMesas(Model model) throws SQLException {
        model.addAttribute("mesas", mesaService.buscarTodos());
        model.addAttribute("layout", getLayout() + "/layout");
        return "atendente/mesas";
    }

    // so pra testar
    @GetMapping("/mesas/{id}")
    public String listarPedidos(Model model) throws SQLException {
        return "pedido/lista_cozinha";
    }
}

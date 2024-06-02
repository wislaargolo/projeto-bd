package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.model.InstanciaProduto;
import br.ufrn.imd.bd.model.Pedido;
import br.ufrn.imd.bd.model.PedidoInstancia;
import br.ufrn.imd.bd.model.Produto;
import br.ufrn.imd.bd.service.MesaService;
import br.ufrn.imd.bd.service.PedidoService;
import br.ufrn.imd.bd.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;


    @Autowired
    private ProdutoService produtoService;

    @GetMapping
    public String listarPedidos(Model model) throws SQLException {
        model.addAttribute("pedidos", pedidoService.buscarTodos());
        return "pedido/lista";
    }

    @GetMapping("/novo")
    public String criarFormPedido(Model model) throws SQLException {
        List<InstanciaProduto> produtos = produtoService.buscarTodos();
        model.addAttribute("pedido", new Pedido());
        model.addAttribute("produtos", produtos);
        return "pedido/formulario";
    }

    @PostMapping
    public String salvarPedido(@ModelAttribute Pedido pedido, BindingResult bindingResult) throws SQLException {

        if (bindingResult.hasErrors()) {
            return "pedido/formulario";
        }

        List<PedidoInstancia> instancias = pedido.getProdutos().stream()
                .filter(pi -> pi.getInstanciaProduto() != null && pi.getQuantidade() != null && pi.getQuantidade() > 0)
                .collect(Collectors.toList());

        pedido.getAtendente().setId(1L);
        pedido.getConta().setId(1L);
        pedido.setProdutos(instancias);
        pedidoService.salvar(pedido);

        return "redirect:/pedidos";
    }
}

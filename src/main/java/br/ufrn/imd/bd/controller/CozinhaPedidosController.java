package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.model.InstanciaProduto;
import br.ufrn.imd.bd.model.Mesa;
import br.ufrn.imd.bd.model.Pedido;
import br.ufrn.imd.bd.model.PedidoInstancia;
import br.ufrn.imd.bd.service.PedidoService;
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
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cozinha/pedidos")
public class CozinhaPedidosController {

    @Autowired
    private PedidoService pedidoService;


    @Autowired
    private ProdutoService produtoService;

    @GetMapping
    public String listarPedidos(Model model) throws SQLException {
        model.addAttribute("pedidos", pedidoService.buscarPedidosPorTuno());
        return "pedido/lista_cozinha";
    }

    @GetMapping("/{id}/editar")
    public String editarFormPedido(Model model, @PathVariable Long id) throws SQLException {
        model.addAttribute("pedido", pedidoService.buscarPorId(id));
        return "pedido/formulario_cozinha";
    }

    @PostMapping("/editar")
    public String editarPedido(@ModelAttribute @Valid Pedido pedido, BindingResult bindingResult) throws SQLException, EntidadeJaExisteException {
        if (bindingResult.hasErrors()) {
            return "pedido/formulario_cozinha";
        }
        pedidoService.atualizar(pedido);
        return "redirect:/cozinha/pedidos";
    }
}

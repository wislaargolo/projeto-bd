package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.exceptions.EntidadeNaoExisteException;
import br.ufrn.imd.bd.model.Pedido;
import br.ufrn.imd.bd.model.PedidoInstancia;
import br.ufrn.imd.bd.model.enums.ProgressoPedido;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        model.addAttribute("pedidos", pedidoService.buscarPedidosAbertos());
        return "pedido/lista_cozinha";
    }

    @GetMapping("/{id}")
    public String buscarPedido(Model model, @PathVariable Long id, RedirectAttributes redirectAttributes) throws SQLException {
        try {
            model.addAttribute("pedido", pedidoService.buscarPorIdComProdutos(id));
        } catch (EntidadeNaoExisteException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/cozinha/pedidos";
        }
        return "pedido/pedido_cozinha";
    }

    @PostMapping("/{id}/status")
    public String atualizarStatusPedido(@PathVariable Long id, @RequestParam("progressoPedido") String status, RedirectAttributes attributes)  {
        Pedido pedido = new Pedido();
        pedido.setId(id);
        pedido.setProgressoPedido(ProgressoPedido.valueOf(status));
        try {
            pedidoService.atualizar(pedido);
            attributes.addFlashAttribute("successMessage", "Status atualizado com sucesso!");
        } catch (EntidadeJaExisteException e) {
            attributes.addFlashAttribute("error", "Erro ao atualizar o status.");
            throw new RuntimeException(e);
        } catch (SQLException e) {
            attributes.addFlashAttribute("error", "Erro ao atualizar o status.");
            throw new RuntimeException(e);
        }
        return "redirect:/cozinha/pedidos";
    }


}

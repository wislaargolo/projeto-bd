package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.exceptions.EntidadeNaoExisteException;
import br.ufrn.imd.bd.service.MesaService;
import br.ufrn.imd.bd.service.PedidoService;
import br.ufrn.imd.bd.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;

public abstract class AtendentePedidosController {

    @Autowired
    private MesaService mesaService;

    @Autowired
    private PedidoService pedidoService;

    public abstract String getLayout();

    @GetMapping("/mesas")
    public String listarMesas(Model model) throws SQLException {
        model.addAttribute("mesas", mesaService.buscarTodos());
        model.addAttribute("layout", getLayout() + "/layout");
        return "pedido/mesas";
    }

    //incompleto
    @GetMapping("/mesas/{id}")
    public String listarPedidosPorMesa(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) throws SQLException {
        try {
            model.addAttribute("pedidos", pedidoService.buscarPedidosPorMesa(id));
            model.addAttribute("layout", getLayout() + "/layout");
        } catch (EntidadeNaoExisteException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/" + getLayout() + "/pedidos/mesas";
        }
        return "pedido/lista_atendentes";
    }
}

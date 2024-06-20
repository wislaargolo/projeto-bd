package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.exceptions.EntidadeNaoExisteException;
import br.ufrn.imd.bd.model.*;
import br.ufrn.imd.bd.service.AtendenteService;
import br.ufrn.imd.bd.service.MesaService;
import br.ufrn.imd.bd.service.PedidoService;
import br.ufrn.imd.bd.service.ProdutoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;

public abstract class AtendentePedidosController {

    @Autowired
    private MesaService mesaService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private AtendenteService atendenteService;

    @Autowired
    private ProdutoService produtoService;


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
            model.addAttribute("mesa", mesaService.buscarPorId(id));
        } catch (EntidadeNaoExisteException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/" + getLayout() + "/pedidos/mesas";
        }
        return "pedido/lista_atendentes";
    }

    @GetMapping("/novo/{id}")
    public String mostrarFormParaAddPedido(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes, HttpSession session) throws SQLException{
        try {
            Pedido pedido = new Pedido();
            //pedido.setAtendente(atendenteService.buscarPorId(1L));
            Conta conta = new Conta();
            conta.setMesa(mesaService.buscarPorId(id));
            pedido.setConta(conta);
            session.setAttribute("pedido", pedido);
            model.addAttribute("pedido", pedido);
            model.addAttribute("todosProdutos", produtoService.buscarTodos());
            model.addAttribute("mesa", mesaService.buscarPorId(id));

        } catch (EntidadeNaoExisteException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/" + getLayout() + "/pedidos/mesas";
        }
        return "pedido/form_novo_pedido";
    }

    @PostMapping("/add/instancia/{id}")
    public String addInstanciaEmPedido(@PathVariable Long id,
                                     Model model,
                                     RedirectAttributes redirectAttributes, HttpSession session) throws SQLException {
        try {

            Pedido pedido = (Pedido) session.getAttribute("pedido");
            if (pedido == null) {
                throw new IllegalStateException("Pedido not found in session");
            }

            InstanciaProduto instanciaProduto = produtoService.buscarPorId(id);
            PedidoInstancia pedidoInstancia = new PedidoInstancia(instanciaProduto, 1);

            pedido.addProduto(pedidoInstancia);
            session.setAttribute("pedido", pedido);
            model.addAttribute("pedido", pedido);
            model.addAttribute("todosProdutos", produtoService.buscarTodos());

        } catch (EntidadeNaoExisteException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/" + getLayout() + "/pedidos/mesas";
        }
        return "pedido/form_novo_pedido";
    }

    @PostMapping("/novo/salvar")
    public String salvarNovoPedido(@ModelAttribute("pedido") Pedido pedido, Authentication authentication, Model model) throws SQLException{

        /*for (PedidoInstancia item : pedido.getProdutos()) {
            System.out.println(item.getQuantidade());
        }*/
        /*try {
            Funcionario funcionario = (Funcionario) authentication.getPrincipal() ;
            Atendente atendente = funcionario.getId();
            Conta conta = new Conta();
            conta.setAtendente();

        }
*/
        return "redirect:/" + getLayout() + "/pedidos/mesas";
    }
}

package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.exceptions.EntidadeNaoExisteException;
import br.ufrn.imd.bd.model.*;
import br.ufrn.imd.bd.model.enums.ProgressoPedido;
import br.ufrn.imd.bd.model.enums.StatusConta;
import br.ufrn.imd.bd.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;
import java.util.List;

public abstract class AtendentePedidosController {

    @Autowired
    private MesaService mesaService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private AtendenteService atendenteService;

    @Autowired
    private ContaService contaService;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private CancelamentoService cancelamentoService;


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
            model.addAttribute("mesa", mesaService.buscarPorId(id));
            List<Pedido> pedidos = pedidoService.buscarPedidosPorMesa(id);
            if(pedidos != null && !pedidos.isEmpty()) {
                model.addAttribute("total", contaService.obterTotal(pedidos.get(0).getConta().getId()));
            }
            model.addAttribute("pedidos",pedidos);
            model.addAttribute("layout", getLayout() + "/layout");
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
            Conta conta = contaService.buscarPorMesa(id);
            if (conta != null) {
                pedido.setConta(conta);
            }else {
                pedido.getConta().setMesa(mesaService.buscarPorId(id));
            }

            session.setAttribute("pedido", pedido);
            model.addAttribute("pedido", pedido);
            model.addAttribute("todosProdutos", produtoService.buscarTodosDisponiveis());
            model.addAttribute("mesa", mesaService.buscarPorId(id));
            model.addAttribute("layout", getLayout() + "/layout");

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
            model.addAttribute("todosProdutos", produtoService.buscarTodosDisponiveis());
            model.addAttribute("layout", getLayout() + "/layout");

        } catch (EntidadeNaoExisteException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/" + getLayout() + "/pedidos/mesas";
        }
        return "pedido/form_novo_pedido";
    }

    /*@PostMapping("/add/instancia/{id}")
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

            boolean exists = false;
            for (PedidoInstancia pi : pedido.getProdutos()) {
                if (pi.getInstanciaProduto().getProduto().getId().equals(instanciaProduto.getProduto().getId())) {
                    pi.setQuantidade(pi.getQuantidade() + 1); // Incrementa a quantidade se o produto já estiver no pedido
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                pedido.addProduto(pedidoInstancia);
            }

            session.setAttribute("pedido", pedido);
            model.addAttribute("pedido", pedido);
            model.addAttribute("todosProdutos", produtoService.buscarTodosDisponiveis());
            model.addAttribute("layout", getLayout() + "/layout");

        } catch (EntidadeNaoExisteException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/" + getLayout() + "/pedidos/mesas";
        }
        return "pedido/form_novo_pedido";
    }*/

    @GetMapping("/rmv/instancia")
    public String removeInstanciaEmPedido(@RequestParam("id") Long id,
                                          Model model,
                                          RedirectAttributes redirectAttributes, HttpSession session) throws SQLException {

        try {
            Pedido pedido = (Pedido) session.getAttribute("pedido");
            if (pedido == null) {
                throw new IllegalStateException("Pedido not found in session");
            }

            InstanciaProduto instanciaProd = produtoService.buscarPorId(id);
            PedidoInstancia aux = null;
            for (PedidoInstancia item : pedido.getProdutos()) {
                if (item.getInstanciaProduto().getProduto().getId().equals(instanciaProd.getId())) {
                    aux = item;
                }
            }
            if (aux != null) pedido.getProdutos().remove(aux);

            session.setAttribute("pedido", pedido);
            model.addAttribute("pedido", pedido);
            model.addAttribute("todosProdutos", produtoService.buscarTodosDisponiveis());
            model.addAttribute("layout", getLayout() + "/layout");

        } catch (EntidadeNaoExisteException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/" + getLayout() + "/pedidos/mesas";
        }
        return "pedido/form_novo_pedido";
    }


    @PostMapping("/novo/salvar")
    public String salvarNovoPedido(Authentication authentication,
                                   Model model,
                                   HttpSession session,
                                   @ModelAttribute Pedido pedidoAtualizado,
                                   RedirectAttributes redirectAttributes) throws SQLException {

        try {
            Pedido pedido = (Pedido) session.getAttribute("pedido");
            if (pedido == null) {
                throw new IllegalStateException("Pedido not found in session");
            }

            // Atualizar o pedido na sessão com os dados do formulário
            for (int i = 0; i < pedido.getProdutos().size(); i++ ) {
                pedido.getProdutos().get(i).setQuantidade(pedidoAtualizado.getProdutos().get(i).getQuantidade());
            }

            Funcionario funcionario = (Funcionario) authentication.getPrincipal();
            Atendente atendente = atendenteService.buscarPorId(funcionario.getId());
            pedido.setAtendente(atendente);

            if (pedido.getConta().getId() == null) {
                contaService.salvar(pedido.getConta());
            }

            pedido.setProgressoPedido(ProgressoPedido.SOLICITADO);
            pedidoService.salvar(pedido);
            model.addAttribute("layout", getLayout() + "/layout");

        } catch (EntidadeNaoExisteException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/" + getLayout() + "/pedidos/mesas";
    }

    @PostMapping("/conta/{id}/solicitar")
    public String solicitarConta(@PathVariable Long id, RedirectAttributes redirectAttributes) throws SQLException{
        try {
            Conta conta = new Conta();
            conta.setStatusConta(StatusConta.SOLICITADA);
            conta.setId(id);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Funcionario funcionarioLogado = (Funcionario) auth.getPrincipal();
            conta.setAtendente(new Atendente(funcionarioLogado.getId()));

            contaService.atualizar(conta);

            redirectAttributes.addFlashAttribute("sucesso","Conta solicitada com sucesso!");
            return "redirect:/" + getLayout() + "/pedidos/mesas/" + conta.getMesa().getId();
        } catch (EntidadeNaoExisteException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/" + getLayout() + "/pedidos/mesas";
        }

    }

    @PostMapping("/{id}/cancelar")
    public String cancelarPedido(@PathVariable Long id, @RequestParam Long mesaId, RedirectAttributes redirectAttributes) throws SQLException {
        try {
            cancelamentoService.cancelarPedido(id);
            redirectAttributes.addFlashAttribute("sucesso", "Pedido cancelado com sucesso!");
            return "redirect:/" + getLayout() + "/pedidos/mesas/" + mesaId;
        } catch (EntidadeNaoExisteException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/" + getLayout() + "/pedidos/mesas";
        }
    }

    @GetMapping("/cancelar")
    public String mostarCancelarItemDePedido(@RequestParam Long id, Model model, RedirectAttributes redirectAttributes) throws SQLException, EntidadeNaoExisteException {

        try {
            Pedido pedido = pedidoService.buscarPorIdComProdutos(id);
            model.addAttribute("pedido", pedido);
            model.addAttribute("layout", getLayout() + "/layout");
        } catch (EntidadeNaoExisteException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/" + getLayout() + "/pedidos/mesas";
        }

        return "pedido/cancelar_item";
    }

    @GetMapping("/removerProduto/{pedidoId}/{produtoId}")
    public String removerProdutoDoPedido(@PathVariable Long pedidoId, @PathVariable Long produtoId, RedirectAttributes redirectAttributes) {
        try {
            Pedido pedido = pedidoService.buscarPorIdComProdutos(pedidoId);
            InstanciaProduto instanciaProduto = produtoService.buscarPorId(produtoId);

            if (pedido.getProdutos().size() == 1) {
                cancelamentoService.cancelarPedido(pedidoId);
                redirectAttributes.addFlashAttribute("sucesso", "Pedido cancelado com sucesso!");
                return "redirect:/" + getLayout() + "/pedidos/mesas/" + pedido.getConta().getMesa().getId();
            }


            cancelamentoService.cancelarItemDoPedido(pedidoId, produtoId);
            redirectAttributes.addFlashAttribute("success", "Produto removido com sucesso");

            return "redirect:/" + getLayout() + "/pedidos/mesas/" + pedido.getConta().getMesa().getId();
        } catch (EntidadeNaoExisteException | SQLException e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao remover o produto: " + e.getMessage());
            return "redirect:/" + getLayout() + "/pedidos/mesas";
        }

    }
}


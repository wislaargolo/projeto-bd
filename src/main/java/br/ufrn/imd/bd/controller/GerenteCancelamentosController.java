package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.model.Caixa;
import br.ufrn.imd.bd.model.Cancelamento;
import br.ufrn.imd.bd.service.CaixaService;
import br.ufrn.imd.bd.service.CancelamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/gerente/cancelamentos")
public class GerenteCancelamentosController {

    @Autowired
    private CancelamentoService cancelamentoService;

    @GetMapping
    public String listarCancelamentos(Model model) throws SQLException {
        Map<Long, List<Cancelamento>> cancelamentos = cancelamentoService.agruparCancelamentosPorPedido();
        model.addAttribute("cancelamentos", cancelamentos);
        return "gerente/cancelamentos";
    }
}

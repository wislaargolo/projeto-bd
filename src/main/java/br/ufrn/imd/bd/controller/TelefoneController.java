package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.model.Funcionario;
import br.ufrn.imd.bd.model.Telefone;
import br.ufrn.imd.bd.service.FuncionarioService;
import br.ufrn.imd.bd.service.TelefoneService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@Controller
@RequestMapping("/funcionarios")
public class TelefoneController {

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private TelefoneService telefoneService;

    @GetMapping("/{id}/telefones")
    public String listarTelefonesFuncionario(Model model, @PathVariable Long id) throws SQLException {
        model.addAttribute("funcionario", funcionarioService.buscarPorId(id));
        model.addAttribute("telefones", telefoneService.buscarTelefonesPorFuncionarioId(id));
        return "telefone/lista";
    }

    @GetMapping("/{id}/telefones/novo")
    public String criarFormTelefone(Model model, @PathVariable Long id) throws SQLException {
        model.addAttribute("telefone", new Telefone(id));
        return "telefone/formulario";
    }

    @PostMapping("/{id}/telefones")
    public String salvarTelefone(@ModelAttribute @Valid Telefone telefone, BindingResult bindingResult, @PathVariable Long id) throws SQLException, EntidadeJaExisteException, EntidadeJaExisteException {
        if (bindingResult.hasErrors()) {
            return "telefone/formulario";
        }
        telefoneService.salvar(telefone);
        return String.format("redirect:/funcionarios/%s/telefones", id);
    }

    @GetMapping("/{id}/telefones/{telefone}/editar")
    public String editarFormTelefone(Model model, @PathVariable Long id, @PathVariable String telefone) throws SQLException {
        model.addAttribute("telefone", new Telefone(id, telefone));
        return "telefone/formulario";
    }

    @PostMapping("/{id}/telefones/{telefoneAntigo}/editar")
    public String editarTelefone(@ModelAttribute @Valid Telefone telefone, BindingResult bindingResult, @PathVariable Long id, @PathVariable String telefoneAntigo) throws SQLException {
        telefoneService.atualizar(new Telefone(id, telefoneAntigo), telefone);
        return String.format("redirect:/funcionarios/%s/telefones", id);
    }

    @GetMapping("/{id}/telefones/{telefone}/excluir")
    public String excluirTelefone(@PathVariable String telefone, @PathVariable(name = "id") Long funcionarioId) throws SQLException {
        telefoneService.deletar(telefone, funcionarioId);
        return String.format("redirect:/funcionarios/%s/telefones", funcionarioId);
    }
}

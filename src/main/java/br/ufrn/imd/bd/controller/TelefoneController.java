package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.exceptions.EntidadeNaoExisteException;
import br.ufrn.imd.bd.model.Funcionario;
import br.ufrn.imd.bd.model.Telefone;
import br.ufrn.imd.bd.model.TelefoneKey;
import br.ufrn.imd.bd.service.FuncionarioService;
import br.ufrn.imd.bd.service.TelefoneService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;

public abstract class TelefoneController {

    public abstract String getTipo();

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private TelefoneService telefoneService;

    @GetMapping("/{id}/telefones")
    public String listarTelefonesFuncionario(Model model, @PathVariable Long id) throws SQLException, EntidadeNaoExisteException {
        model.addAttribute("tipo", getTipo());

        model.addAttribute("funcionario", funcionarioService.buscarPorId(id));
        model.addAttribute("telefones", telefoneService.buscarTelefonesPorFuncionarioId(id));
        return "telefone/lista";
    }

    @GetMapping("/{id}/telefones/novo")
    public String criarFormTelefone(Model model, @PathVariable Long id) throws SQLException, EntidadeNaoExisteException {
        model.addAttribute("tipo", getTipo());
        model.addAttribute("telefone", new Telefone(id));
        model.addAttribute("edicao", false);
        return "telefone/formulario";
    }

    @PostMapping("/{id}/telefones/salvar")
    public String salvarTelefone(@ModelAttribute @Valid Telefone telefone, BindingResult bindingResult,
                                 @PathVariable Long id, RedirectAttributes redirectAttributes, Model model) throws SQLException, EntidadeNaoExisteException {

        model.addAttribute("tipo", getTipo());
        model.addAttribute("edicao", false);

        if (bindingResult.hasErrors()) {
            return "telefone/formulario";
        }

        try {
            telefoneService.salvar(telefone);
        } catch (EntidadeJaExisteException e) {
            model.addAttribute("error", e.getMessage());
            return "telefone/formulario";
        }
        return String.format("redirect:/gerente/%s/%s/telefones", getTipo(),id);
    }

    @GetMapping("/{id}/telefones/editar")
    public String editarFormTelefone(Model model, @PathVariable Long id, @RequestParam("telefone") String telefone, RedirectAttributes redirectAttributes) throws SQLException {

        model.addAttribute("edicao", true);

        try {
            model.addAttribute("telefone", telefoneService.buscarPorChave(new TelefoneKey(id, telefone)));
            model.addAttribute("tipo", getTipo());
        } catch (EntidadeNaoExisteException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return String.format("redirect:/gerente/%s/%s/telefones", getTipo(),id);
        }

        return "telefone/formulario";
    }
    @PostMapping("/{id}/telefones/editar")
    public String editarTelefone(@ModelAttribute @Valid Telefone telefone, BindingResult bindingResult, @PathVariable Long id,
                                 @RequestParam("telefoneAntigo") String telefoneAntigo, RedirectAttributes redirectAttributes, Model model) throws SQLException {

        model.addAttribute("tipo", getTipo());
        model.addAttribute("edicao", true);

        if (bindingResult.hasErrors()) {
            model.addAttribute("telefoneAntigo", telefoneAntigo);
            return "telefone/formulario";
        }

        try {
            telefoneService.atualizar(new Telefone(id, telefoneAntigo), telefone);
        } catch (EntidadeNaoExisteException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return String.format("redirect:/%s/%s/telefones", getTipo(),id);
        } catch (EntidadeJaExisteException e) {
            model.addAttribute("telefoneAntigo", telefoneAntigo);
            model.addAttribute("error", e.getMessage());
            return "telefone/formulario";
        }
        return String.format("redirect:/gerente/%s/%s/telefones", getTipo(), id);
    }


    @PostMapping("/{id}/telefones/excluir")
    public String excluirTelefone(@RequestParam("telefone") String telefone, @PathVariable Long id, RedirectAttributes redirectAttributes) throws SQLException, EntidadeNaoExisteException {
        try {
            telefoneService.deletar(telefone, id);
        } catch (EntidadeNaoExisteException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return String.format("redirect:/gerente/%s/%s/telefones", getTipo(), id);
        }
        return String.format("redirect:/gerente/%s/%s/telefones", getTipo(),id);
    }

}

package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.controller.dto.TelefoneDTO;
import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
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
@RequestMapping("/telefones")
public class TelefoneController {

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private TelefoneService telefoneService;

    @GetMapping
    public String listarTodosOsTelefones(Model model) throws SQLException {;
        model.addAttribute("telefones", telefoneService.buscarTodos());
        return "telefone/lista";
    }

    @GetMapping("/novo")
    public String criarFormTelefone(Model model) throws SQLException {
        model.addAttribute("telefoneDTO", new TelefoneDTO());
        model.addAttribute("funcionarios", funcionarioService.buscarTodos());
        return "telefone/formulario";
    }

    @PostMapping
    public String salvarCaixa(@ModelAttribute @Valid TelefoneDTO telefoneDTO, BindingResult bindingResult) throws SQLException, EntidadeJaExisteException {
        if (bindingResult.hasErrors()) {
            return "telefone/formulario";
        }
        if(telefoneDTO.getTelefoneHidden() == null) {
            telefoneService.salvar(telefoneDTO.getTelefoneNovo());
        } else {
            telefoneService.atualizar(telefoneDTO.getTelefoneHidden(), telefoneDTO.getTelefoneNovo());
        }
        return "redirect:/telefones";
    }

    @GetMapping("/editar/{telefone}/funcionario/{id}")
    public String excluirAtendente(Model model, @PathVariable String telefone, @PathVariable(name = "id") Long funcionarioId) throws SQLException {
        model.addAttribute("telefoneDTO", telefoneService.getTelefoneDTO(telefone, funcionarioId));
        model.addAttribute("funcionarios", funcionarioService.buscarTodos());
        return "telefone/formulario";
    }

    @GetMapping("/excluir/{telefone}/funcionario/{id}")
    public String excluirAtendente(@PathVariable String telefone, @PathVariable(name = "id") Long funcionarioId) throws SQLException {
        telefoneService.deletar(telefone, funcionarioId);
        return "redirect:/telefones";
    }
}
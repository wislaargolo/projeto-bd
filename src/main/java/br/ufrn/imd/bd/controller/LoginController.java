package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.model.Funcionario;
import br.ufrn.imd.bd.service.TelefoneService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;

import java.sql.SQLException;

@Controller
public class LoginController {

    @Autowired
    private TelefoneService telefoneService;


    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/perfil")
    public String perfil(Model model, Authentication authentication, HttpServletRequest request) throws SQLException {
        Funcionario funcionario = (Funcionario) authentication.getPrincipal();
//        String currentURI = request.getRequestURI();
//        model.addAttribute("returnUrl", currentURI);

        String layout = "garcom/layout";
        if (funcionario.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_GERENTE"))) {
            layout = "gerente/layout";
        } else if (funcionario.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CAIXA"))) {
            layout = "caixa/layout";
        } else if (funcionario.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_COZINHEIRO"))) {
            layout = "cozinha/layout";
        }

        model.addAttribute("telefones", telefoneService.buscarTelefonesPorFuncionarioId(funcionario.getId()));
        model.addAttribute("layout", layout);
        model.addAttribute("funcionario", funcionario);
        return "perfil";
    }



}

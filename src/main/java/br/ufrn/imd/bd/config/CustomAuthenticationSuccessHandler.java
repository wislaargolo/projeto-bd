package br.ufrn.imd.bd.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CAIXA"))) {
            response.sendRedirect("/caixa/contas");
        } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_COZINHEIRO"))) {
            response.sendRedirect("/cozinha/pedidos");
        } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_GARCOM"))) {
            response.sendRedirect("/garcom/pedidos/mesas");
        } else {
            response.sendRedirect("/gerente/pedidos/mesas");
        }
    }

}

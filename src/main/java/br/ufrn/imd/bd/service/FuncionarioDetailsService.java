package br.ufrn.imd.bd.service;

import br.ufrn.imd.bd.connection.DatabaseConfig;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Service
public class FuncionarioDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try (Connection connection = DatabaseConfig.getConnection()) {
            String sql = "SELECT f.login, f.senha, " +
                    "EXISTS (SELECT * FROM caixa WHERE id_funcionario = f.id_funcionario) AS is_caixa, " +
                    "EXISTS (SELECT * FROM cozinheiro WHERE id_funcionario = f.id_funcionario) AS is_cozinheiro, " +
                    "(SELECT tipo FROM atendente WHERE id_funcionario = f.id_funcionario) AS tipo_atendente " +
                    "FROM funcionario f WHERE f.login = ? AND f.is_ativo = TRUE";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                throw new UsernameNotFoundException("Usuário não encontrado: " + username);
            }

            String password = rs.getString("senha");
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            if (rs.getBoolean("is_caixa")) {
                authorities.add(new SimpleGrantedAuthority("ROLE_CAIXA"));
            }
            if (rs.getBoolean("is_cozinheiro")) {
                authorities.add(new SimpleGrantedAuthority("ROLE_COZINHEIRO"));
            }
            String tipoAtendente = rs.getString("tipo_atendente");
            if ("GERENTE".equals(tipoAtendente)) {
                authorities.add(new SimpleGrantedAuthority("ROLE_GERENTE"));
            } else if ("GARCOM".equals(tipoAtendente)) {
                authorities.add(new SimpleGrantedAuthority("ROLE_GARCOM"));
            }

            return new User(username, password, authorities);
        } catch (Exception e) {
            throw new RuntimeException("ERRO >> Erro ao acessar o banco de dados", e);
        }
    }
}

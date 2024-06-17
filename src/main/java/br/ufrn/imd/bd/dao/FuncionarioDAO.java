package br.ufrn.imd.bd.dao;

import br.ufrn.imd.bd.connection.DatabaseConfig;
import br.ufrn.imd.bd.dao.util.ResultSetUtil;
import br.ufrn.imd.bd.model.Cozinheiro;
import br.ufrn.imd.bd.model.Funcionario;
import org.springframework.cglib.core.Local;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;

@Component
public class FuncionarioDAO extends AbstractDAO<Funcionario, Long> {

    @Override
    public Funcionario mapearResultado(ResultSet rs) throws SQLException {
        return this.mapearResultado(rs, "");
    }


    public Funcionario mapearResultado(ResultSet rs, String prefixo) throws SQLException {
        Funcionario funcionario = new Funcionario();
        funcionario.setId(rs.getLong(prefixo + "id_funcionario"));
        funcionario.setNome(rs.getString(prefixo + "nome"));
        funcionario.setLogin(ResultSetUtil.getValue(rs, prefixo + "login", String.class));
        funcionario.setSenha(ResultSetUtil.getValue(rs, prefixo + "senha", String.class));
        funcionario.setEmail(ResultSetUtil.getValue(rs, prefixo + "email", String.class));
        funcionario.setDataCadastro(ResultSetUtil.getLocalDate(rs, prefixo + "data_cadastro"));
        funcionario.setAtivo(ResultSetUtil.getBooleanFromInteger(rs, "is_ativo", prefixo));

        return funcionario;
    }

    public Funcionario mapearParcialmente(ResultSet rs) throws SQLException {
        Funcionario funcionario = new Funcionario();
        funcionario.setId(rs.getLong("id_funcionario"));

        return funcionario;
    }

    @Override
    public String getNomeTabela() {
        return "funcionario";
    }

    @Override
    public Funcionario salvar(Connection conn, Funcionario funcionario) throws SQLException {
        String sql = String.format("INSERT INTO %s (nome, login, senha, email) VALUES (?, ?, ?, ?)", getNomeTabela());

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, funcionario.getNome());
            stmt.setString(2, funcionario.getLogin());
            stmt.setString(3, funcionario.getSenha());
            stmt.setString(4, funcionario.getEmail());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("ERRO >> A inserção do funcionário falhou, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    funcionario.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("ERRO >> A inserção do funcionário falhou, nenhum ID gerado.");
                }
            }
        }

        return funcionario;
    }

    @Override
    public void atualizar(Connection conn, Funcionario... funcionarios) throws SQLException {
        if (funcionarios.length != 1) {
            throw new IllegalArgumentException("ERRO >> Apenas um funcionário para atualização.");
        }

        Funcionario novo = funcionarios[0];

        StringBuilder sqlBuilder = new StringBuilder("UPDATE ");
        sqlBuilder.append(getNomeTabela());
        sqlBuilder.append(" SET nome = ?, login = ?, email = ?, is_ativo = ?");
        if (novo.getSenha() != null && !novo.getSenha().isBlank()) {
            sqlBuilder.append(", senha = ?");
        }
        sqlBuilder.append(" WHERE id_funcionario = ?");
        String sql = sqlBuilder.toString();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, novo.getNome());
            stmt.setString(2, novo.getLogin());
            stmt.setString(3, novo.getEmail());
            stmt.setBoolean(4, novo.getAtivo());
            int parameterIndex = 5;
            if (novo.getSenha() != null && !novo.getSenha().isBlank()) {
                stmt.setString(parameterIndex++, novo.getSenha());
            }
            stmt.setLong(parameterIndex, novo.getId());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("ERRO >> Atualização falhou.");
            }
        }
    }

    public Funcionario carregarPorLogin(String login) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String sql = "SELECT f.*, " +
                    "EXISTS (SELECT * FROM caixa WHERE id_funcionario = f.id_funcionario) AS is_caixa, " +
                    "EXISTS (SELECT * FROM cozinheiro WHERE id_funcionario = f.id_funcionario) AS is_cozinheiro, " +
                    "(SELECT tipo FROM atendente WHERE id_funcionario = f.id_funcionario) AS tipo_atendente " +
                    "FROM funcionario f WHERE f.login = ? AND f.is_ativo = TRUE";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, login);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        throw new UsernameNotFoundException("Usuário não encontrado: " + login);
                    }

                    Funcionario funcionario = mapearResultado(rs);

                    if (rs.getBoolean("is_caixa")) {
                        funcionario.adicionaAutoridade("CAIXA");
                    }
                    if (rs.getBoolean("is_cozinheiro")) {
                        funcionario.adicionaAutoridade("COZINHEIRO");
                    }
                    String tipoAtendente = rs.getString("tipo_atendente");
                    if ("GERENTE".equals(tipoAtendente)) {
                        funcionario.adicionaAutoridade("GERENTE");
                    } else if ("GARCOM".equals(tipoAtendente)) {
                        funcionario.adicionaAutoridade("GARCOM");
                    }

                    return funcionario;
                }
            }


        }

    }

    public Map<Funcionario, String> buscarPorLogin(Connection conn, String login) throws SQLException {
        String sql = "SELECT f.*, " +
                "EXISTS (SELECT * FROM caixa WHERE id_funcionario = f.id_funcionario) AS is_caixa, " +
                "EXISTS (SELECT * FROM cozinheiro WHERE id_funcionario = f.id_funcionario) AS is_cozinheiro, " +
                "(SELECT tipo FROM atendente WHERE id_funcionario = f.id_funcionario) AS tipo_atendente " +
                "FROM funcionario f WHERE f.login = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Funcionario funcionario = mapearResultado(rs);
                    String papel = determinarPapel(rs);
                    return Collections.singletonMap(funcionario, papel);
                }
            }
        }
        return null;
    }

    private String determinarPapel(ResultSet rs) throws SQLException {
        if ("GERENTE".equals(rs.getString("tipo_atendente"))) {
            return "GERENTE";
        } else if ("GARCOM".equals(rs.getString("tipo_atendente"))) {
            return "GARCOM";
        } else if (rs.getBoolean("is_caixa")) {
            return "CAIXA";
        } else if (rs.getBoolean("is_cozinheiro")) {
            return "COZINHEIRO";
        }
        return "FUNCIONARIO";
    }

}
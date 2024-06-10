package br.ufrn.imd.bd.dao;

import br.ufrn.imd.bd.dao.util.ResultSetUtil;
import br.ufrn.imd.bd.model.Funcionario;
import org.springframework.stereotype.Component;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

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
        funcionario.setLogin(rs.getString(prefixo + "login"));
        funcionario.setSenha(rs.getString(prefixo + "senha"));
        funcionario.setEmail(rs.getString(prefixo + "email"));
        funcionario.setDataCadastro(rs.getObject(prefixo + "data_cadastro", LocalDate.class));
        funcionario.setAtivo(ResultSetUtil.getBooleanFromInteger(rs, "is_ativo", prefixo));

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

    public Funcionario buscarPorParametros(Connection conn, String email, String login) throws SQLException {
        String sql = String.format("SELECT * FROM %s WHERE email = ? OR login = ?", getNomeTabela());

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, login);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultado(rs);
                }
            }
        }
        return null;
    }
}
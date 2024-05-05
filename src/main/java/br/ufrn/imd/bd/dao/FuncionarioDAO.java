package br.ufrn.imd.bd.dao;

import br.ufrn.imd.bd.model.Funcionario;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

@Component
public class FuncionarioDAO extends AbstractDAOImpl<Funcionario, Long> {

    @Override
    protected Funcionario mapearResultado(ResultSet rs) throws SQLException {
        Funcionario funcionario = new Funcionario();
        funcionario.setId(rs.getLong("id"));
        funcionario.setNome(rs.getString("nome"));
        funcionario.setLogin(rs.getString("login"));
        funcionario.setSenha(rs.getString("senha"));
        funcionario.setEmail(rs.getString("email"));
        funcionario.setDataCadastro(rs.getObject("data_cadastro", LocalDate.class));
        return funcionario;
    }

    @Override
    public String getNomeTabela() {
        return "funcionarios";
    }

    @Override
    public Funcionario salvar(Funcionario funcionario) {
        String sql = String.format("INSERT INTO %s (nome, login, senha, email, data_cadastro) VALUES (?, ?, ?, ?, ?)", getNomeTabela());
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, funcionario.getNome());
            stmt.setString(2, funcionario.getLogin());
            stmt.setString(3, funcionario.getSenha());
            stmt.setString(4, funcionario.getEmail());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("ERRO >> A inserção do funcionário falhou, nenhuma linha afetada.");
            }

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                funcionario.setId(rs.getLong(1));
            } else {
                throw new SQLException("ERRO >> Falha ao obter o ID do funcionário inserido.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return funcionario;
    }

    public void atualizar(Funcionario... funcionarios)  {
        if (funcionarios.length != 1) {
            throw new IllegalArgumentException("ERRO >> Apenas um funcionário para atualização.");
        }

        Funcionario novo = funcionarios[0];

        String sql = String.format(
                "UPDATE %s SET nome = ?, login = ?, senha = ?, email = ?, data_cadastro = ? WHERE id_funcionario = ?",
                getNomeTabela()
        );

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, novo.getNome());
            stmt.setString(2, novo.getLogin());
            stmt.setString(3, novo.getSenha());
            stmt.setString(4, novo.getEmail());
            stmt.setObject(5, novo.getDataCadastro()); // Atribui LocalDateTime usando setObject
            stmt.setLong(6, novo.getId());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("ERRO >> Atualização falhou.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}

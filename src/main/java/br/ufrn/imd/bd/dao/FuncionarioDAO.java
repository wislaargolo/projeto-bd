package br.ufrn.imd.bd.dao;

import br.ufrn.imd.bd.model.Funcionario;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Repository
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
        String sql = "INSERT INTO funcionarios (nome, login, senha, email) VALUES (?, ?, ?, ?) RETURNING id, data_cadastro";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, funcionario.getNome());
            stmt.setString(2, funcionario.getLogin());
            stmt.setString(3, funcionario.getSenha());
            stmt.setString(4, funcionario.getEmail());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                funcionario.setId(rs.getLong("id"));
                funcionario.setDataCadastro(rs.getObject("data_cadastro", LocalDate.class));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return funcionario;
    }

    @Override
    public void atualizar(Funcionario funcionario) {

        String sql = "UPDATE " + getNomeTabela() +
                " SET nome = ?, login = ?, senha = ?, email = ? WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, funcionario.getNome());
            stmt.setString(2, funcionario.getLogin());
            stmt.setString(3, funcionario.getSenha());
            stmt.setString(4, funcionario.getEmail());
            stmt.setLong(5, funcionario.getId());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("ERRO >> Atualização falhou.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}

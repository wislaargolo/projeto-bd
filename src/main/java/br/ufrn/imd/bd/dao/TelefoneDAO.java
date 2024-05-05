package br.ufrn.imd.bd.dao;

import br.ufrn.imd.bd.model.Funcionario;
import br.ufrn.imd.bd.model.Telefone;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class TelefoneDAO extends AbstractDAOImpl<Telefone, Long> {
    @Override
    public Telefone salvar(Telefone telefone) {
        String sql = String.format(
                "INSERT INTO %s (telefone, funcionario_id) VALUES (?, ?) RETURNING *",
                getNomeTabela()
        );
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false); // Inicia uma transação

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, telefone.getTelefone());
                stmt.setLong(2, telefone.getFuncionario().getId());

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    telefone = mapearResultado(rs); // Mapeia o ResultSet para um objeto Telefone
                }
                connection.commit(); // Confirma a transação
            } catch (SQLException e) {
                connection.rollback(); // Reverte a transação em caso de erro
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return telefone;
    }

    @Override
    public void atualizar(Telefone... telefones) {

        if (telefones.length != 2) {
            throw new IllegalArgumentException("ERRO >> São necessários exatamente dois telefones para realizar a atualização.");
        }

        Telefone telefoneAntigo = telefones[0];
        Telefone telefoneNovo = telefones[1];

        String sql = String.format(
                "UPDATE %s SET telefone = ? WHERE telefone = ? AND funcionario_id = ?",
                getNomeTabela()
        );
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false); // Inicia uma transação

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, telefoneNovo.getTelefone());
                stmt.setString(2, telefoneAntigo.getTelefone());
                stmt.setLong(3, telefoneAntigo.getFuncionario().getId());

                int linhasAfetadas = stmt.executeUpdate();
                if (linhasAfetadas == 0) {
                    throw new SQLException("ERRO >> Atualização falhou.");
                }

                connection.commit(); // Confirma a transação
            } catch (SQLException e) {
                connection.rollback(); // Reverte a transação em caso de erro
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Telefone mapearResultado(ResultSet rs) throws SQLException {
        Telefone telefone = new Telefone();
        telefone.setTelefone(rs.getString("telefone"));

        Funcionario funcionario = new Funcionario();
        funcionario.setId(rs.getLong("funcionario_id"));
        telefone.setFuncionario(funcionario);

        return telefone;
    }

    @Override
    public String getNomeTabela() {
        return "telefones";
    }
}

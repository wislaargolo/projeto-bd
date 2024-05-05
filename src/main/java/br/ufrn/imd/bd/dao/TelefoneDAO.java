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
                "INSERT INTO %s (telefone, funcionario_id) VALUES (?, ?)",
                getNomeTabela()
        );

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, telefone.getTelefone());
            stmt.setLong(2, telefone.getIdFuncionario());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("ERRO >> A inserção do telefone falhou, nenhuma linha afetada.");
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
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, telefoneNovo.getTelefone());
            stmt.setString(2, telefoneAntigo.getTelefone());
            stmt.setLong(3, telefoneAntigo.getIdFuncionario());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("ERRO >> Atualização falhou.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Telefone mapearResultado(ResultSet rs) throws SQLException {
        Telefone telefone = new Telefone();
        telefone.setTelefone(rs.getString("telefone"));
        telefone.setIdFuncionario(rs.getLong("id_funcionario"));

        return telefone;
    }

    @Override
    public String getNomeTabela() {
        return "telefones";
    }
}

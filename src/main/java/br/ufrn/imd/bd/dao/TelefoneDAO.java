package br.ufrn.imd.bd.dao;

import br.ufrn.imd.bd.model.Telefone;
import br.ufrn.imd.bd.model.TelefoneKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class TelefoneDAO extends AbstractDAO<Telefone, TelefoneKey> {

    @Autowired
    private FuncionarioDAO funcionarioDAO;

    @Override
    public Telefone mapearResultado(ResultSet rs) throws SQLException {
        Telefone telefone = new Telefone();
        telefone.setFuncionario(funcionarioDAO.mapearResultado(rs));
        telefone.setTelefone(rs.getString("telefone_funcionario"));
        return telefone;
    }

    @Override
    public String getNomeTabela() {
        return "telefone";
    }

    @Override
    public Telefone salvar(Connection conn, Telefone telefone) throws SQLException {
        String sql = String.format("INSERT INTO %s (id_funcionario, telefone_funcionario) VALUES (?, ?)", getNomeTabela());

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, telefone.getFuncionario().getId());
            stmt.setString(2, telefone.getTelefone());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("ERRO >> A inserção do funcionário falhou, nenhuma linha afetada.");
            }
        }

        return telefone;
    }

    @Override
    public void atualizar(Connection conn, Telefone... telefones) throws SQLException {

        if (telefones.length != 2) {
            throw new IllegalArgumentException("ERRO >> São necessários exatamente dois telefones para realizar a atualização.");
        }

        Telefone telefoneAntigo = telefones[0];
        Telefone telefoneNovo = telefones[1];

        String sql = String.format(
                "UPDATE %s SET telefone_funcionario = ? WHERE telefone_funcionario = ? AND id_funcionario = ?",
                getNomeTabela()
        );

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, telefoneNovo.getTelefone());
            stmt.setString(2, telefoneAntigo.getTelefone());
            stmt.setLong(3, telefoneAntigo.getFuncionario().getId());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("ERRO >> Atualização falhou.");
            }
        }
    }

    @Override
    public void deletarPorId(Connection conn, TelefoneKey key) throws SQLException {
        String sql = String.format("DELETE FROM %s WHERE id_funcionario = ? AND telefone_funcionario = ?", getNomeTabela());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, key.getIdFuncionario());
            stmt.setString(2, key.getTelefoneFuncionario());
            stmt.executeUpdate();
        }
    }

    public List<Telefone> buscarPorFuncionarioId(Long id) throws SQLException {
        List<Telefone> resultados = new ArrayList<>();
        String sql = String.format("SELECT * from %s NATURAL JOIN %s id_funcionario = %s", getNomeTabela(), funcionarioDAO.getNomeTabela(), id);
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                resultados.add(mapearResultado(rs));
            }
        }
        return resultados;
    }

    public boolean existeTelefone(Connection conn, TelefoneKey key) throws SQLException {
        String sql = String.format("SELECT * FROM %s WHERE id_funcionario = ? AND telefone_funcionario = ?", getNomeTabela());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, key.getIdFuncionario());
            stmt.setObject(2, key.getTelefoneFuncionario());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return true;
            }
        }
        return false;
    }
}
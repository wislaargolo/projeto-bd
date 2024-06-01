package br.ufrn.imd.bd.dao;

import br.ufrn.imd.bd.model.Atendente;
import br.ufrn.imd.bd.model.enums.TipoAtendente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class AtendenteDAO extends AbstractDAO<Atendente, Long> {

    @Autowired
    private FuncionarioDAO funcionarioDAO;

    @Override
    public Atendente mapearResultado(ResultSet rs) throws SQLException {
        Atendente atendente = new Atendente(funcionarioDAO.mapearResultado(rs));
        atendente.setTipo(TipoAtendente.valueOf(rs.getString("tipo")));
        return atendente;
    }

    @Override
    public String getNomeTabela() {
        return "atendentes";
    }

    @Override
    protected String getBuscarTodosQuery() {
        return String.format("SELECT * FROM %s a JOIN funcionarios f ON a.id = f.id", getNomeTabela());
    }

    @Override
    protected String getBuscarPorIdQuery() {
        return String.format("SELECT * FROM %s a JOIN funcionarios f on f.id = a.id WHERE a.id = ?", getNomeTabela());
    }

    @Override
    public Atendente salvar(Connection conn, Atendente atendente) throws SQLException {
        Atendente atendenteSalvo = new Atendente(funcionarioDAO.salvar(conn, atendente));
        atendenteSalvo.setTipo(atendente.getTipo());
        String sql = String.format("INSERT INTO %s (id, tipo) VALUES (?, ?)", getNomeTabela());

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, atendenteSalvo.getId());
            stmt.setString(2, atendenteSalvo.getTipo().toString());
            stmt.executeUpdate();
        }

        return atendenteSalvo;
    }

    @Override
    public void atualizar(Connection conn, Atendente... atendentes) throws SQLException {
        if (atendentes.length != 1) {
            throw new IllegalArgumentException("ERRO >> Apenas um atendente para atualização.");
        }

        String sql = String.format(
                "UPDATE %s SET tipo = ? WHERE id = ?",
                getNomeTabela()
        );

        Atendente atendente = atendentes[0];
        funcionarioDAO.atualizar(conn, atendente);

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, atendente.getTipo().toString());
            stmt.setLong(2, atendente.getId());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("ERRO >> Atualização falhou.");
            }
        }
    }

    @Override
    public void deletarPorId(Connection conn, Long id) throws SQLException {
        String sql = String.format("DELETE FROM %s WHERE id = ?", getNomeTabela());
        funcionarioDAO.deletarPorId(conn, id);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            stmt.executeUpdate();
        }
    }
}
package br.ufrn.imd.bd.dao;

import br.ufrn.imd.bd.model.Cozinheiro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class CozinheiroDAO extends AbstractDAO<Cozinheiro, Long> {

    @Autowired
    private FuncionarioDAO funcionarioDAO;

    @Override
    public Cozinheiro mapearResultado(ResultSet rs) throws SQLException {
        return new Cozinheiro(funcionarioDAO.mapearResultado(rs));
    }

    @Override
    public String getNomeTabela() {
        return "cozinheiros";
    }

    @Override
    protected String getBuscarTodosQuery() {
        return String.format("SELECT f.* FROM %s c JOIN funcionarios f ON c.id = f.id", getNomeTabela());
    }

    @Override
    public Cozinheiro buscarPorId(Long id) throws SQLException {
        return new Cozinheiro(funcionarioDAO.buscarPorId(id));
    }

    @Override
    public Cozinheiro salvar(Connection conn, Cozinheiro cozinheiro) throws SQLException {
        Cozinheiro cozinheiroSalvo = new Cozinheiro(funcionarioDAO.salvar(conn, cozinheiro));
        String sql = String.format("INSERT INTO %s (id) VALUES (?)", getNomeTabela());

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, cozinheiroSalvo.getId());
            stmt.executeUpdate();
        }

        return cozinheiroSalvo;
    }

    @Override
    public void atualizar(Connection conn, Cozinheiro... cozinheiros) throws SQLException {
        if (cozinheiros.length != 1) {
            throw new IllegalArgumentException("ERRO >> Apenas um cozinheiro para atualização.");
        }

        Cozinheiro cozinheiro = cozinheiros[0];
        funcionarioDAO.atualizar(conn, cozinheiro);
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
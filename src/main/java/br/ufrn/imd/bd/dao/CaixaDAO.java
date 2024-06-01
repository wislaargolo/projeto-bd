package br.ufrn.imd.bd.dao;

import br.ufrn.imd.bd.model.Caixa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class CaixaDAO extends AbstractDAO<Caixa,Long> {

    @Autowired
    private FuncionarioDAO funcionarioDAO;

    @Override
    public Caixa mapearResultado(ResultSet rs) throws SQLException {
        return new Caixa(funcionarioDAO.mapearResultado(rs));
    }

    @Override
    public String getNomeTabela() {
        return "caixas";
    }

    @Override
    protected String getBuscarTodosQuery() {
        return String.format("SELECT f.* FROM %s c JOIN funcionarios f ON c.id = f.id", getNomeTabela());
    }

    @Override
    public Caixa buscarPorId(Long id) throws SQLException {
        return new Caixa(funcionarioDAO.buscarPorId(id));
    }

    @Override
    public Caixa salvar(Connection conn, Caixa caixa) throws SQLException {
        Caixa caixaSalvo = new Caixa(funcionarioDAO.salvar(conn, caixa));
        String sql = String.format("INSERT INTO %s (id) VALUES (?)", getNomeTabela());

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, caixaSalvo.getId());
            stmt.executeUpdate();
        }

        return caixaSalvo;
    }

    @Override
    public void atualizar(Connection conn, Caixa... caixas) throws SQLException {
        if (caixas.length != 1) {
            throw new IllegalArgumentException("ERRO >> Apenas um caixa para atualização.");
        }

        Caixa caixa = caixas[0];
        funcionarioDAO.atualizar(conn, caixa);
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

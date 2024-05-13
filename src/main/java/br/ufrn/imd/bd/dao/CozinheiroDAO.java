package br.ufrn.imd.bd.dao;

import br.ufrn.imd.bd.model.Cozinheiro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class CozinheiroDAO extends AbstractDAOImpl<Cozinheiro, Long> {

    @Autowired
    private FuncionarioDAO funcionarioDAO;

    @Override
    protected Cozinheiro mapearResultado(ResultSet rs) throws SQLException {
        return new Cozinheiro(funcionarioDAO.mapearResultado(rs));
    }

    @Override
    public String getNomeTabela() {
        return "cozinheiros";
    }

    @Override
    public List<Cozinheiro> buscarTodos() throws SQLException {
        List<Cozinheiro> resultados = new ArrayList<>();
        String sql = "SELECT f.* FROM cozinheiros c JOIN funcionarios f ON c.id = f.id";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                resultados.add(mapearResultado(rs));
            }
        }
        return resultados;
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
    public Cozinheiro buscarPorId(Long id) throws SQLException {
        return new Cozinheiro(funcionarioDAO.buscarPorId(id));
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
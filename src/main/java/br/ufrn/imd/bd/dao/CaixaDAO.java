package br.ufrn.imd.bd.dao;

import br.ufrn.imd.bd.model.Caixa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class CaixaDAO extends AbstractDAOImpl<Caixa,Long> {

    @Autowired
    private FuncionarioDAO funcionarioDAO;

    @Override
    protected Caixa mapearResultado(ResultSet rs) throws SQLException {
        return new Caixa(funcionarioDAO.mapearResultado(rs));
    }

    @Override
    public String getNomeTabela() {
        return "caixas";
    }

    @Override
    public List<Caixa> buscarTodos() throws SQLException {
        List<Caixa> resultados = new ArrayList<>();
        String sql = "SELECT f.* FROM caixas c JOIN funcionarios f ON c.id = f.id";

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
    public Caixa buscarPorId(Long id) throws SQLException {
        return new Caixa(funcionarioDAO.buscarPorId(id));
    }
}

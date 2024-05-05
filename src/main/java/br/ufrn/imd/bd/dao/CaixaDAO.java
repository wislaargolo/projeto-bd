package br.ufrn.imd.bd.dao;

import br.ufrn.imd.bd.model.Caixa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    public List<Caixa> buscarTodos() {
        List<Caixa> resultados = new ArrayList<>();
        String sql = "SELECT f.* FROM caixas c JOIN funcionarios f ON c.id = f.id";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                resultados.add(mapearResultado(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultados;
    }
    @Override
    public Caixa salvar(Caixa caixa) {
        String sql = String.format("INSERT INTO %s (id) VALUES (?)", getNomeTabela());

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, caixa.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("ERRO >> A inserção do caixa falhou, nenhuma linha afetada.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return caixa;
    }

    @Override
    public void atualizar(Caixa... caixas) {
        funcionarioDAO.atualizar(caixas);
    }

    @Override
    public Caixa buscarPorId(Long id) {
       return new Caixa(funcionarioDAO.buscarPorId(id));
    }
}

package br.ufrn.imd.bd.dao;


import br.ufrn.imd.bd.model.Funcionario;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CaixaDAO extends FuncionarioDAO {

    @Override
    public List<Funcionario> buscarTodos() {
        List<Funcionario> resultados = new ArrayList<>();
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
    public String getNomeTabela() {
        return "caixas";
    }
}

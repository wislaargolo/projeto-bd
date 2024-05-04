package br.ufrn.imd.bd.dao;

import br.ufrn.imd.bd.model.Caixa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CaixaDAO extends AbstractDAOImpl<Caixa,Long> {

    private final FuncionarioDAO funcionarioDAO;

    public CaixaDAO(FuncionarioDAO funcionarioDAO) {
        this.funcionarioDAO = funcionarioDAO;
    }

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
        Caixa caixaSalvo = new Caixa(funcionarioDAO.salvar(caixa));

        String sql = "INSERT INTO caixas (id) VALUES (?)";
        try (Connection conn = funcionarioDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, caixaSalvo.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return caixaSalvo;
    }

    @Override
    public void atualizar(Caixa entidade) {

    }

    @Override
    public Caixa buscarPorId(Long id) {
        return new Caixa(funcionarioDAO.buscarPorId(id));
    }
}

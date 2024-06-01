package br.ufrn.imd.bd.dao;

import br.ufrn.imd.bd.model.InstanciaProduto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.LocalDateTime;

@Component
public class InstanciaProdutoDAO extends AbstractDAO<InstanciaProduto, Long> {

    @Autowired
    private ProdutoDAO produtoDAO;

    @Override
    public InstanciaProduto mapearResultado(ResultSet rs) throws SQLException {

        InstanciaProduto instanciaProduto = new InstanciaProduto();
        instanciaProduto.setId(rs.getLong("id_produto_instancia"));
        instanciaProduto.setValor(rs.getDouble("valor"));
        instanciaProduto.setAtivo(rs.getBoolean("is_ativo"));
        instanciaProduto.setData(rs.getObject("data", LocalDateTime.class));
        instanciaProduto.setProduto(produtoDAO.mapearResultado(rs));
        return instanciaProduto;
    }

    @Override
    public String getNomeTabela() {
        return "produto_instancias";
    }

    @Override
    protected String getBuscarTodosQuery() {
        return String.format("SELECT * FROM %s pi JOIN produtos p ON pi.id_produto = p.id_produto WHERE pi.is_ativo = true;", getNomeTabela());
    }

    @Override
    protected String getBuscarPorIdQuery() {
        return String.format("SELECT * FROM %s pi JOIN produtos p ON pi.id_produto = p.id_produto WHERE id_produto_instancia = ?", getNomeTabela());
    }

    @Override
    public InstanciaProduto salvar(Connection conn, InstanciaProduto instanciaProduto) throws SQLException {
        String sql = String.format("INSERT INTO %s (valor, id_produto) VALUES (?, ?)", getNomeTabela());

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDouble(1, instanciaProduto.getValor());
            stmt.setLong(2, instanciaProduto.getProduto().getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("ERRO >> A inserção de instancia produto falhou, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    instanciaProduto.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("ERRO >> A inserção do instancia produto falhou, nenhum ID gerado.");
                }
            }
        }

        return instanciaProduto;
    }

    @Override
    public void atualizar(Connection conn, InstanciaProduto... instanciaProdutos) throws SQLException {
        if (instanciaProdutos.length != 1) {
            throw new IllegalArgumentException("ERRO >> Apenas um funcionário para atualização.");
        }

        InstanciaProduto novo = instanciaProdutos[0];

        String sql = String.format(
                "UPDATE %s SET valor = ?, is_ativo = ?, id_produto = ? WHERE id_produto_instancia = ?",
                getNomeTabela()
        );

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, novo.getValor());
            stmt.setBoolean(2, novo.getAtivo());
            stmt.setLong(3, novo.getProduto().getId());
            stmt.setLong(4, novo.getId());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("ERRO >> Atualização falhou.");
            }
        }
    }

    public boolean existeProdutoNome(Connection conn, InstanciaProduto instanciaProduto) throws SQLException {
        String sql = "SELECT COUNT(*) FROM produto_instancias pi " +
                "JOIN produtos p ON p.id_produto = pi.id_produto " +
                "WHERE pi.is_ativo = true ";

        if (instanciaProduto.getId() != null) {
            sql += "AND pi.id_produto_instancia != ? ";
        }

        sql += "AND p.nome = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int parameterIndex = 1;
            if (instanciaProduto.getId() != null) {
                stmt.setObject(parameterIndex++, instanciaProduto.getId());
            }
            stmt.setString(parameterIndex, instanciaProduto.getProduto().getNome());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
        }
        return false;
    }
}

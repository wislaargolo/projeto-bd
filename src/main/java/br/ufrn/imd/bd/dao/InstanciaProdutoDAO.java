package br.ufrn.imd.bd.dao;

import br.ufrn.imd.bd.dao.util.ResultSetUtil;
import br.ufrn.imd.bd.model.Funcionario;
import br.ufrn.imd.bd.model.InstanciaProduto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class InstanciaProdutoDAO extends AbstractDAO<InstanciaProduto, Long> {

    @Autowired
    private ProdutoDAO produtoDAO;

    @Override
    public InstanciaProduto mapearResultado(ResultSet rs) throws SQLException {

        InstanciaProduto instanciaProduto = new InstanciaProduto();
        instanciaProduto.setId(rs.getLong("id_instancia_produto"));
        instanciaProduto.setValor(ResultSetUtil.getDouble(rs, "valor"));
        instanciaProduto.setAtivo(ResultSetUtil.getBooleanFromInteger(rs, "is_ativo", ""));
        instanciaProduto.setData(ResultSetUtil.getLocalDate(rs, "data"));
        instanciaProduto.setProduto(produtoDAO.mapearResultado(rs));
        return instanciaProduto;
    }


    @Override
    public String getNomeTabela() {
        return "instancia_produto";
    }

    @Override
    protected String getBuscarTodosQuery() {
        return String.format("SELECT * FROM %s AS ip NATURAL JOIN %s WHERE ip.is_ativo = true;", getNomeTabela(), produtoDAO.getNomeTabela());
    }

    @Override
    protected String getBuscarPorIdQuery() {
        return String.format("SELECT * FROM %s AS ip NATURAL JOIN %s WHERE ip.id_instancia_produto = ?", getNomeTabela(), produtoDAO.getNomeTabela());
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
                "UPDATE %s SET valor = ?, id_produto = ?, is_ativo = ? WHERE id_instancia_produto = ?",
                getNomeTabela()
        );

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, novo.getValor());
            stmt.setLong(2, novo.getProduto().getId());
            stmt.setBoolean(3, novo.getAtivo());
            stmt.setLong(4, novo.getId());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("ERRO >> Atualização falhou.");
            }
        }
    }

    public InstanciaProduto buscarUltimaInstanciaPorDescricao(Connection conn, String descricao) throws SQLException {
        String sql = "SELECT * FROM instancia_produto " +
                    "NATURAL JOIN produto " +
                    "WHERE produto.descricao = ? " +
                    "ORDER BY instancia_produto.data DESC " +
                    "LIMIT 1";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, descricao);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultado(rs);
                }
            }
        }
        return null;
    }
}

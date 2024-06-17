package br.ufrn.imd.bd.dao;

import br.ufrn.imd.bd.model.Atendente;
import br.ufrn.imd.bd.model.Cozinheiro;
import br.ufrn.imd.bd.model.enums.TipoAtendente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public Atendente mapearResultado(ResultSet rs, String prefixo) throws SQLException {
        Atendente atendente = new Atendente(funcionarioDAO.mapearResultado(rs, prefixo));
        atendente.setTipo(TipoAtendente.valueOf(rs.getString(prefixo + "tipo")));
        return atendente;
    }

    @Override
    public String getNomeTabela() {
        return "atendente";
    }

    @Override
    protected String getBuscarTodosQuery() {
        return String.format("SELECT * FROM %s NATURAL JOIN %s WHERE is_ativo = true", getNomeTabela(), funcionarioDAO.getNomeTabela());
    }

    @Override
    protected String getBuscarPorIdQuery() {
        return String.format("SELECT * FROM %s NATURAL JOIN %s WHERE id_funcionario = ?", getNomeTabela(), funcionarioDAO.getNomeTabela());
    }

    @Override
    public Atendente salvar(Connection conn, Atendente atendente) throws SQLException {
        Atendente atendenteSalvo = new Atendente(funcionarioDAO.salvar(conn, atendente));
        atendenteSalvo.setTipo(atendente.getTipo());
        String sql = String.format("INSERT INTO %s (id_funcionario, tipo) VALUES (?, ?)", getNomeTabela());

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

        Atendente atendente = atendentes[0];
        funcionarioDAO.atualizar(conn, atendente);

        if (atendente.getTipo() != null) {
            String sql = String.format(
                    "UPDATE %s SET tipo = ? WHERE id_funcionario = ?",
                    getNomeTabela()
            );

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, atendente.getTipo().toString());
                stmt.setLong(2, atendente.getId());

                int linhasAfetadas = stmt.executeUpdate();
                if (linhasAfetadas == 0) {
                    throw new SQLException("ERRO >> Atualização falhou, nenhum registro afetado.");
                }
            }
        }
    }


    @Override
    public void deletar(Connection conn, Long id) throws SQLException {
        funcionarioDAO.deletar(conn, id);
    }

    public List<Atendente> buscarPorTipo(TipoAtendente tipo) throws SQLException {
        String sql = String.format("SELECT * FROM %s NATURAL JOIN %s WHERE is_ativo = true AND tipo = ?", getNomeTabela(), funcionarioDAO.getNomeTabela());
        List<Atendente> resultados = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tipo.toString()); // Configura o tipo de atendente no SQL
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    resultados.add(mapearResultado(rs, ""));
                }
            }
        }
        return resultados;
    }

//    public Atendente buscarPorLogin(Connection conn, String login) throws SQLException {
//        String sql = String.format("SELECT f.* FROM %s AS f NATURAL JOIN %s WHERE f.login = ?", funcionarioDAO.getNomeTabela(), getNomeTabela());
//        return new Atendente(funcionarioDAO.buscarPorLogin(conn, sql, login));
//    }

}
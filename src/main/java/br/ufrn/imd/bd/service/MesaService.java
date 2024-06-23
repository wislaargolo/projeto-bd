package br.ufrn.imd.bd.service;

import br.ufrn.imd.bd.connection.DatabaseUtil;
import br.ufrn.imd.bd.dao.MesaDAO;
import br.ufrn.imd.bd.exceptions.EntidadeJaExisteException;
import br.ufrn.imd.bd.exceptions.EntidadeNaoExisteException;
import br.ufrn.imd.bd.model.Mesa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
public class MesaService {

    @Autowired
    private MesaDAO mesaDAO;

    @Autowired
    private DatabaseUtil databaseUtil;


    public List<Mesa> buscarTodos() throws SQLException {
        return mesaDAO.buscarTodos();
    }

    public Mesa buscarPorId(Long id) throws SQLException, EntidadeNaoExisteException {

        Mesa mesa = mesaDAO.buscarPorChave(id);
        if(mesa == null) {
            throw new EntidadeNaoExisteException("Mesa não encontrada.");
        }
        return mesa;
    }

    public Mesa salvar(Mesa mesa) throws SQLException, EntidadeJaExisteException {
        try (Connection conn = databaseUtil.getConnection()) {
            try {
                mesa = mesaDAO.salvar(conn, mesa);
                return mesa;
            } catch (SQLException e) {
                if (e.getErrorCode() == 1062) {
                    reativarSeInativo(conn, mesa);
                    throw new EntidadeJaExisteException("Uma mesa inativa com essa identificação foi reativada.");
                } else {
                    throw e;
                }
            }
        }
    }

    public void atualizar(Mesa mesa) throws EntidadeJaExisteException, SQLException, EntidadeNaoExisteException {

        buscarPorId(mesa.getId());

        try (Connection conn = databaseUtil.getConnection()) {
            try {
                mesaDAO.atualizar(conn, mesa);
            } catch (SQLException e) {
                if (e.getErrorCode() == 1062) {
                    reativarSeInativo(conn, mesa);
                    throw new EntidadeJaExisteException("Uma mesa inativa com essa identificação foi reativada.");
                } else {
                    throw e;
                }
            }
        }
    }

    public void deletar(Long id) throws SQLException, EntidadeNaoExisteException {

        buscarPorId(id);

        try (Connection conn = databaseUtil.getConnection()){
            mesaDAO.deletar(conn, id);
        }
    }

    private void reativarSeInativo(Connection conn, Mesa mesa) throws SQLException, EntidadeJaExisteException {
        Mesa existente = mesaDAO.buscarPorIdentificacao(conn, mesa.getIdentificacao());
        if (existente != null && !existente.getAtivo()) {
            existente.setAtivo(true);
            mesaDAO.atualizar(conn, existente);
        } else if (existente != null) {
            throw new EntidadeJaExisteException("Já existe uma mesa ativa com essa identificação!");
        }
    }

}

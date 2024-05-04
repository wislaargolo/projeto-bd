package br.ufrn.imd.bd.service;

import br.ufrn.imd.bd.dao.CaixaDAO;
import br.ufrn.imd.bd.model.Caixa;
import br.ufrn.imd.bd.model.Funcionario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CaixaService {

    @Autowired
    private CaixaDAO caixaDAO;

    public List<Funcionario> buscarTodos() {
        return caixaDAO.buscarTodos();
    }

    public Funcionario buscarPorId(Long id) {
        return caixaDAO.buscarPorId(id);
    }

    public Funcionario salvar(Caixa caixa) {
        return caixaDAO.salvar(caixa);
    }

    public void atualizar(Caixa caixa) {
        caixaDAO.atualizar(caixa);
    }

    public void deletarPorId(Long id) {
        caixaDAO.deletarPorId(id);
    }
}

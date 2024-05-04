package br.ufrn.imd.bd.dao;


import org.springframework.stereotype.Repository;

@Repository
public class CaixaDAO extends FuncionarioDAO {

    @Override
    public String getTableName() {
        return "caixas";
    }
}

package br.ufrn.imd.bd.exceptions;

public class FuncionarioJaExisteException extends Exception{
    public FuncionarioJaExisteException(String message) {
        super(message);
    }
}

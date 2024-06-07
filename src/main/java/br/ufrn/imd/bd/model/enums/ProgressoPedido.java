package br.ufrn.imd.bd.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ProgressoPedido {

    SOLICITADO,
    PREPARANDO,
    REJEITADO,
    ALTERADO,
    CANCELADO,
    FINALIZADO;

    public static List<ProgressoPedido> listaCozinha() {
        return Arrays.stream(values())
                .filter(progresso -> progresso != CANCELADO)
                .collect(Collectors.toList());
    }

}



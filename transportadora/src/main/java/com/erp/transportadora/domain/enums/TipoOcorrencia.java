package com.erp.transportadora.domain.enums;

public enum TipoOcorrencia {
    ENTREGUE("Entregue"),
    COLETA("Coleta"),
    TROCA("Troca"),
    RECUSADO("Recusado"),
    CANCELADA("Cancelada");

    private final String descricao;

    TipoOcorrencia(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
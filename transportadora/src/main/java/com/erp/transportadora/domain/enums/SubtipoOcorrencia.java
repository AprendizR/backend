package com.erp.transportadora.domain.enums;

import lombok.Getter;

@Getter
public enum SubtipoOcorrencia {
    // Entregas
    ENTREGA_COMPLETA("Entrega Completa"),
    ENTREGA_PARCIAL("Entrega Parcial"),
    ENTREGA_RECUSADA("Entrega Recusada"),

    // Coletas
    COLETA_COMPLETA("Coleta Completa"),
    COLETA_PARCIAL("Coleta Parcial"),
    COLETA_NAO_EFETUADA("Coleta Não Efetuada"),

    // Trocas
    TROCA_COMPLETA("Troca Completa"),
    TROCA_PARCIAL("Troca Parcial"),
    TROCA_NAO_EFETUADA("Troca Não Efetuada"),

    DESTINATARIO_AUSENTE("Destinatário Ausente"),
    ENDERECO_INCORRETO("Endereço Incorreto"),

    // Cancelamentos
    CANCELADA_PELO_CLIENTE("Cancelada pelo Cliente"),
    CANCELADA_OPERACIONAL("Cancelada Operacional");

    private final String descricao;

    SubtipoOcorrencia(String descricao) {
        this.descricao = descricao;
    }

}
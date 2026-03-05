package com.erp.transportadora.domain.enums;

import lombok.Getter;

@Getter
public enum SubtipoOcorrencia {
    ENTREGA_COMPLETA("Entrega Completa"),
    ENTREGA_PARCIAL("Entrega Parcial"),
    ENTREGA_RECUSADA("Entrega Recusada"),

    COLETA_COMPLETA("Coleta Completa"),
    COLETA_PARCIAL("Coleta Parcial"),
    COLETA_NAO_EFETUADA("Coleta Não Efetuada"),

    TROCA_COMPLETA("Troca Completa"),
    TROCA_PARCIAL("Troca Parcial"),
    TROCA_NAO_EFETUADA("Troca Não Efetuada"),

    DESTINATARIO_AUSENTE("Destinatário Ausente"),
    ENDERECO_INCORRETO("Endereço Incorreto"),
    LOCAL_FECHADO("Local Fechado"),

    CANCELADO("Cancelada pelo Cliente");

    private final String descricao;

    SubtipoOcorrencia(String descricao) {
        this.descricao = descricao;
    }

    public StatusNota toStatusNota() {
        return switch (this) {
            case ENTREGA_COMPLETA, ENTREGA_PARCIAL -> StatusNota.ENTREGUE;
            case ENTREGA_RECUSADA, DESTINATARIO_AUSENTE, ENDERECO_INCORRETO, COLETA_NAO_EFETUADA, TROCA_NAO_EFETUADA, LOCAL_FECHADO -> StatusNota.DEVOLVIDO;
            case COLETA_COMPLETA, COLETA_PARCIAL -> StatusNota.COLETA;
            case TROCA_COMPLETA, TROCA_PARCIAL -> StatusNota.TROCA;
            case CANCELADO -> StatusNota.CANCELADA;
        };
    }

}
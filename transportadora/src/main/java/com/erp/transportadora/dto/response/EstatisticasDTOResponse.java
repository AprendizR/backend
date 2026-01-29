package com.erp.transportadora.dto.response;

public record EstatisticasDTOResponse(
        Long totalCargas,
        Long cargasEmRota,
        Long cargasEntregues,
        Long notasEntreguesHoje,
        Long totalNotas,
        Long notasPendentes
) {}
package com.erp.transportadora.dto.response;

import java.util.List;

public record FaturamentoDTOClienteResponse(
        String remetente,
        Long totalNotas,
        List<FaturamentoDTOCidadeResponse> cidades
) {
}

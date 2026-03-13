package com.erp.transportadora.dto.response;

import java.util.List;

public record FaturamentoDTOClienteResponse(
        Long clienteId,
        String cliente,
        Long totalNotas,
        Double totalFrete,
        List<FaturamentoDTOCidadeResponse> cidades
) {}

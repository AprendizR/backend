package com.erp.transportadora.dto.response;

public record FaturamentoDTOCidadeResponse(
        String cidade,
        Long totalNotas,
        Double totalFrete
) {}
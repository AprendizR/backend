package com.erp.transportadora.dto.request;

public record FreteClienteDTORequest(
        Long clienteId,
        String cidade,
        Double valor,
        String tipo,
        Double percentual,
        Double adicional) {
}

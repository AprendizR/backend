package com.erp.transportadora.dto.response;

public record FreteClienteDTOResponse(
        Long id,
        Long clienteId,
        String cidade,
        Double valor) {
}
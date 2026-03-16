package com.erp.transportadora.dto.request;

public record NotaFiscalDTORequest(
        String numero,
        Long clienteId,
        String remetente,
        String destinatario,
        String cep,
        String cidade,
        String endereco,
        Double frete,
        Double valor,
        Integer volumes,
        Double latitude,
        Double longitude
) {}


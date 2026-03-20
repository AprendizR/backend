package com.erp.transportadora.dto.request;

import java.time.LocalDate;

public record NotaFiscalDTORequest(
        String numero,
        LocalDate dataEmissao,
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


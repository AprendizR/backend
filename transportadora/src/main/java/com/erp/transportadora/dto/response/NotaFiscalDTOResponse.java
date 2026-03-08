package com.erp.transportadora.dto.response;

public record NotaFiscalDTOResponse(
        Long id,
        Long ordemServico,
        String numero,
        String remetente,
        String destinatario,
        String cidade,
        String endereco,
        String status,
        Double valor,
        Integer volumes
) {}
package com.erp.transportadora.dto.response;

public record NotaFiscalDTOResumo(
        Long id,
        Long ordemServico,
        String numero,
        Boolean entregue
){}

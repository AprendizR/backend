package com.erp.transportadora.dto.response;

public record NotaFiscalDTOResumo(
        Long id,
        Long ordemServico,
        String numero,
        String destinatario,
        String cidade,
        String remetente,
        boolean entregue,
        boolean temFoto,
        boolean isPdf
) {}
package com.erp.transportadora.dto.request;

import jakarta.validation.constraints.NotBlank;

public record NotaFiscalDTORequest(
        String numero,
        @NotBlank(message = "Remetente é obrigatório")
        String remetente,
        String destinatario,
        String cep,
        String cidade,
        String endereco,
        Double valor,
        Integer volumes
) {}

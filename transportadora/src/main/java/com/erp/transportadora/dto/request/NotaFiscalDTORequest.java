package com.erp.transportadora.dto.request;

import com.erp.transportadora.domain.entity.ClienteEntity;
import jakarta.validation.constraints.NotBlank;

public record NotaFiscalDTORequest(
        String numero,
        Long clienteId,
        @NotBlank(message = "Remetente é obrigatório")
        String remetente,
        String destinatario,
        String cep,
        String cidade,
        String endereco,
        Double frete,
        Double valor,
        Integer volumes
) {}

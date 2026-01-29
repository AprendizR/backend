package com.erp.transportadora.dto.response;

import com.erp.transportadora.domain.enums.SubtipoOcorrencia;
import com.erp.transportadora.domain.enums.TipoOcorrencia;

import java.time.LocalDateTime;

public record OcorrenciaDTOResponse(
        Long id,
        Long ordemServico,
        String numeroNota,
        SubtipoOcorrencia subtipo,
        LocalDateTime dataOcorrencia,
        String nomeRecebedor,
        String observacao,
        String urlFotoComprovante
) {}
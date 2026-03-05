package com.erp.transportadora.dto.request;

import com.erp.transportadora.domain.enums.SubtipoOcorrencia;

public record OcorrenciaDTORequest(
        Long ordemServico,
        SubtipoOcorrencia subtipo,
        String nomeRecebedor,
        String observacao,
        String urlFotoComprovante
) {}
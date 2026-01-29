package com.erp.transportadora.dto.request;

import com.erp.transportadora.domain.enums.SubtipoOcorrencia;
import com.erp.transportadora.domain.enums.TipoOcorrencia;

public record OcorrenciaDTORequest(
        Long ordemServico,
        TipoOcorrencia tipo,
        SubtipoOcorrencia subtipo,
        String nomeRecebedor,
        String observacao,
        String urlFotoComprovante
) {}
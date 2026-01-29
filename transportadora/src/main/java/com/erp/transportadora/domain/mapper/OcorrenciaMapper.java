package com.erp.transportadora.domain.mapper;

import com.erp.transportadora.domain.entity.OcorrenciaEntity;
import com.erp.transportadora.dto.response.OcorrenciaDTOResponse;

public class OcorrenciaMapper {

    public static OcorrenciaDTOResponse toResponse(OcorrenciaEntity entity) {
        return new OcorrenciaDTOResponse(
                entity.getId(),
                entity.getNotaFiscal().getOrdemServico(),
                entity.getNotaFiscal().getNumero(),
                entity.getSubtipo(),
                entity.getDataOcorrencia(),
                entity.getNomeRecebedor(),
                entity.getObservacao(),
                entity.getUrlFotoComprovante()
        );
    }
}
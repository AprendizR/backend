package com.erp.transportadora.domain.mapper;

import com.erp.transportadora.validators.NormalizadorUtils;
import com.erp.transportadora.domain.entity.NotaFiscalEntity;
import com.erp.transportadora.dto.request.NotaFiscalDTORequest;
import com.erp.transportadora.dto.response.NotaFiscalDTOResponse;

import java.time.LocalDateTime;

public class NotaFiscalMapper {

    public static NotaFiscalEntity toEntity(NotaFiscalDTORequest dto) {
        return NotaFiscalEntity.builder()
                .numero(NormalizadorUtils.apenasNumeros(dto.numero()))
                .remetente(NormalizadorUtils.trimUpper(dto.remetente()))
                .destinatario(NormalizadorUtils.trimUpper(dto.destinatario()))
                .cidade(NormalizadorUtils.trimUpper(dto.cidade()))
                .endereco(NormalizadorUtils.trimUpper(dto.endereco()))
                .entregue(false)
                .dataEmissao(LocalDateTime.now())
                .build();
    }

    public static NotaFiscalDTOResponse toResponse(NotaFiscalEntity entity) {
        return new NotaFiscalDTOResponse(
                entity.getId(),
                entity.getOrdemServico(),
                entity.getNumero(),
                entity.getRemetente(),
                entity.getDestinatario(),
                entity.getCidade(),
                entity.getEndereco(),
                entity.getEntregue(),
                entity.getVolumes()
        );
    }
}
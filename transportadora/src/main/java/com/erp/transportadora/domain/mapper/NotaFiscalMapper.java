package com.erp.transportadora.domain.mapper;

import com.erp.transportadora.domain.enums.StatusNota;
import com.erp.transportadora.validators.NormalizadorUtils;
import com.erp.transportadora.domain.entity.NotaFiscalEntity;
import com.erp.transportadora.dto.request.NotaFiscalDTORequest;
import com.erp.transportadora.dto.response.NotaFiscalDTOResponse;

import java.time.LocalDateTime;

public class NotaFiscalMapper {

    public static NotaFiscalEntity toEntity(NotaFiscalDTORequest dto) {
        StatusNota statusEnum;
        try {
            statusEnum = StatusNota.valueOf(dto.status().toUpperCase().trim());
        } catch (IllegalArgumentException | NullPointerException e){
            statusEnum = StatusNota.PENDENTE;
        }
        return NotaFiscalEntity.builder()
                .numero(NormalizadorUtils.apenasNumeros(dto.numero()))
                .remetente(NormalizadorUtils.trimUpper(dto.remetente()))
                .destinatario(NormalizadorUtils.trimUpper(dto.destinatario()))
                .cidade(NormalizadorUtils.trimUpper(dto.cidade()))
                .endereco(NormalizadorUtils.trimUpper(dto.endereco()))
                .status(statusEnum)
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
                entity.getStatus() != null ? entity.getStatus().name() : "PENDENTE",
                entity.getVolumes()
        );
    }
}
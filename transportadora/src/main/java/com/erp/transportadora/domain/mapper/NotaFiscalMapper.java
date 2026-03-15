package com.erp.transportadora.domain.mapper;

import com.erp.transportadora.domain.enums.StatusNota;
import com.erp.transportadora.validators.NormalizadorUtils;
import com.erp.transportadora.domain.entity.NotaFiscalEntity;
import com.erp.transportadora.dto.request.NotaFiscalDTORequest;
import com.erp.transportadora.dto.response.NotaFiscalDTOResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class NotaFiscalMapper {

    public static NotaFiscalEntity toEntity(NotaFiscalDTORequest dto) {
        return NotaFiscalEntity.builder()
                .numero(NormalizadorUtils.apenasNumeros(dto.numero()))
                .remetente(NormalizadorUtils.trimUpper(dto.remetente()))
                .destinatario(NormalizadorUtils.trimUpper(dto.destinatario()))
                .cep(NormalizadorUtils.apenasNumeros(dto.cep()))
                .cidade(NormalizadorUtils.trimUpper(dto.cidade()))
                .endereco(NormalizadorUtils.trimUpper(dto.endereco()))
                .frete(dto.frete())
                .valor(dto.valor())
                .volumes(dto.volumes())
                .status(StatusNota.PENDENTE)
                .dataEmissao(LocalDateTime.now())
                .build();
    }

    public static NotaFiscalDTOResponse toResponse(NotaFiscalEntity entity) {
        return new NotaFiscalDTOResponse(
                entity.getId(),
                entity.getOrdemServico(),
                entity.getNumero(),
                entity.getCliente() != null ? ClienteMapper.toResponse(entity.getCliente()) : null,
                entity.getRemetente(),
                entity.getDestinatario(),
                entity.getCep(),
                entity.getCidade(),
                entity.getEndereco(),
                entity.getFrete(),
                entity.getValor(),
                entity.getVolumes(),
                entity.getStatus() != null ? entity.getStatus().name() : "PENDENTE",
                new ArrayList<>(entity.getFotos())
        );
    }
}
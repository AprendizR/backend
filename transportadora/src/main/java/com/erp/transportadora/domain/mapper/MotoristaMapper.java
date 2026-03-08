package com.erp.transportadora.domain.mapper;

import com.erp.transportadora.validators.NormalizadorUtils;
import com.erp.transportadora.domain.entity.MotoristaEntity;
import com.erp.transportadora.dto.response.MotoristaDTOResponse;
import jakarta.validation.Valid;

public class MotoristaMapper {
    public static MotoristaEntity toEntity(@Valid MotoristaEntity dto) {
        return MotoristaEntity.builder()
                .nome(NormalizadorUtils.trimUpper(dto.getNome()))
                .apelido(NormalizadorUtils.trimUpper(dto.getApelido()))
                .cpf(NormalizadorUtils.apenasNumeros(dto.getCpf()))
                .telefone(NormalizadorUtils.apenasNumeros(dto.getTelefone()))
                .build();
    }

    public static MotoristaDTOResponse toResponse(MotoristaEntity entity) {
        return new MotoristaDTOResponse(
                entity.getId(),
                entity.getNome(),
                entity.getApelido(),
                entity.getCpf(),
                entity.getTelefone()
        );
    }
}
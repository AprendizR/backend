package com.erp.transportadora.domain.mapper;

import com.erp.transportadora.dto.request.MotoristaDTORequest;
import com.erp.transportadora.validators.NormalizadorUtils;
import com.erp.transportadora.domain.entity.MotoristaEntity;
import com.erp.transportadora.dto.response.MotoristaDTOResponse;
import jakarta.validation.Valid;

public class MotoristaMapper {
    public static MotoristaEntity toEntity(MotoristaDTORequest dto) {
        return MotoristaEntity.builder()
                .nome(NormalizadorUtils.trimUpper(dto.nome()))
                .apelido(NormalizadorUtils.trimUpper(dto.apelido()))
                .cpf(NormalizadorUtils.apenasNumeros(dto.cpf()))
                .telefone(NormalizadorUtils.apenasNumeros(dto.telefone()))
                .valorDiaria(dto.valorDiaria() != null ? dto.valorDiaria() : 0.0)
                .build();
    }

    public static MotoristaDTOResponse toResponse(MotoristaEntity entity) {
        return new MotoristaDTOResponse(
                entity.getId(),
                entity.getNome(),
                entity.getApelido(),
                entity.getCpf(),
                entity.getTelefone(),
                entity.getValorDiaria()
        );
    }
}
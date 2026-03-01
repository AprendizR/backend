package com.erp.transportadora.domain.mapper;

import com.erp.transportadora.validators.NormalizadorUtils;
import com.erp.transportadora.domain.entity.ClienteEntity;
import com.erp.transportadora.dto.request.ClienteDTORequest;
import com.erp.transportadora.dto.response.ClienteDTOResponse;

public class ClienteMapper {

    private ClienteMapper() {
    }

    public static ClienteEntity toEntity(ClienteDTORequest dto) {
        return ClienteEntity.builder()
                .nome(NormalizadorUtils.trimUpper(dto.nome()))
                .cnpj(dto.cnpj() != null ? NormalizadorUtils.apenasNumeros(dto.cnpj()) : null)
                .email(dto.email() != null ? NormalizadorUtils.trimLower(dto.email()) : null)
                .cidade(dto.cidade() != null ? NormalizadorUtils.trimLower(dto.cidade()) : null)
                .endereco(dto.endereco() != null ? NormalizadorUtils.trimLower(dto.endereco()) : null)
                .cep(dto.cep() != null ? NormalizadorUtils.apenasNumeros(dto.cep()) : null)
                .build();
    }

    public static ClienteDTOResponse toResponse(ClienteEntity entity) {
        return new ClienteDTOResponse(
                entity.getId(),
                entity.getNome(),
                entity.getCnpj(),
                entity.getEmail(),
                entity.getCidade(),
                entity.getEndereco(),
                entity.getCep()
        );
    }
}
package com.erp.transportadora.domain.mapper;

import com.erp.transportadora.validators.NormalizadorUtils;
import com.erp.transportadora.domain.entity.VeiculoEntity;
import com.erp.transportadora.dto.request.VeiculoDTORequest;
import com.erp.transportadora.dto.response.VeiculoDTOResponse;

public class VeiculoMapper {

    public static VeiculoEntity toEntity(VeiculoDTORequest dto) {
        return VeiculoEntity.builder()
                .placa(NormalizadorUtils.trimUpper(dto.placa()))
                .modelo(NormalizadorUtils.trimUpper(dto.modelo()))
                .ativo(true)
                .build();
    }

    public static VeiculoDTOResponse toResponse(VeiculoEntity veiculo) {
        return new VeiculoDTOResponse(
                veiculo.getId(),
                veiculo.getPlaca(),
                veiculo.getModelo()
        );
    }
}

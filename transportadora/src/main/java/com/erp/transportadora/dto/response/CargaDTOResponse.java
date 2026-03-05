package com.erp.transportadora.dto.response;

import com.erp.transportadora.domain.enums.StatusCarga;

import java.time.LocalDateTime;

public record CargaDTOResponse (
        Long id,
        Long numeroRota,
        StatusCarga statusCarga,
        Long veiculoId,
        Long motoristaId,
        LocalDateTime dataCriacao
) {}

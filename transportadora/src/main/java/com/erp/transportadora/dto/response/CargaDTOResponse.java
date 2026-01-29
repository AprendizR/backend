package com.erp.transportadora.dto.response;

import com.erp.transportadora.domain.enums.Status;

import java.time.LocalDateTime;

public record CargaDTOResponse (
        Long id,
        Long numeroRota,
        Status status,
        Long veiculoId,
        Long motoristaId,
        LocalDateTime dataCriacao
) {}

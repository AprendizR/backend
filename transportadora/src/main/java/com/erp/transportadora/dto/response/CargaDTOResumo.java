package com.erp.transportadora.dto.response;

import com.erp.transportadora.domain.enums.Status;

import java.time.LocalDateTime;

public record CargaDTOResumo(
        Long id,
        Long numeroRota,
        Status status,
        MotoristaDTOResumo motorista,
        VeiculoDTOResumo veiculo,
        LocalDateTime dataCriacao
) {}

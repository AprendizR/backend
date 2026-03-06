package com.erp.transportadora.dto.response;

import com.erp.transportadora.domain.enums.StatusCarga;

import java.time.LocalDateTime;

public record CargaDTOResumo(
        Long id,
        Long numeroRota,
        StatusCarga statusCarga,
        MotoristaDTOResponse motorista,
        VeiculoDTOResponse veiculo,
        LocalDateTime dataCriacao
) {}

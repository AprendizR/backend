package com.erp.transportadora.dto.response;

import com.erp.transportadora.domain.enums.StatusCarga;
import com.erp.transportadora.dto.request.MotoristaDTORequest;
import com.erp.transportadora.dto.request.VeiculoDTORequest;

import java.time.LocalDateTime;

public record CargaDTOResumo(
        Long id,
        Long numeroRota,
        StatusCarga statusCarga,
        MotoristaDTOResponse motorista,
        MotoristaDTOResponse ajudante,
        VeiculoDTOResponse veiculo,
        LocalDateTime dataCriacao
) {}
package com.erp.transportadora.dto.response;

import com.erp.transportadora.domain.enums.StatusCarga;
import com.erp.transportadora.dto.request.MotoristaDTORequest;
import com.erp.transportadora.dto.request.VeiculoDTORequest;

import java.time.LocalDateTime;
import java.util.List;

public record CargaDTODetalhada(
        Long id,
        Long numeroRota,
        StatusCarga statusCarga,
        MotoristaDTORequest motorista,
        VeiculoDTORequest veiculo,
        List<NotaFiscalDTOResumo> notasFiscais,
        LocalDateTime dataCriacao
) {}

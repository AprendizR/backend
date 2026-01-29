package com.erp.transportadora.dto.response;

import com.erp.transportadora.domain.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

public record CargaDTODetalhada(
        Long id,
        Long numeroRota,
        Status status,
        MotoristaDTOResumo motorista,
        VeiculoDTOResumo veiculo,
        List<NotaFiscalDTOResumo> notasFiscais,
        LocalDateTime dataCriacao
) {}

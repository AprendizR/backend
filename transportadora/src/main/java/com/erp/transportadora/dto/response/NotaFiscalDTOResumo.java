package com.erp.transportadora.dto.response;

import com.erp.transportadora.domain.enums.Status;
import com.erp.transportadora.domain.enums.StatusNota;

public record NotaFiscalDTOResumo(
        Long id,
        Long ordemServico,
        String numero,
        Boolean entregue
){}

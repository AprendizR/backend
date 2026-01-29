package com.erp.transportadora.dto.request;

import com.erp.transportadora.validators.ValidCPF;
import com.erp.transportadora.validators.ValidTelefone;

public record MotoristaDTORequest (
        String nome,

        @ValidCPF
        String cpf,

        @ValidTelefone
        String telefone
){}

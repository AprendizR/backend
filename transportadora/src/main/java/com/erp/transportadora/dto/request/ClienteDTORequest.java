package com.erp.transportadora.dto.request;

import com.erp.transportadora.validators.ValidCNPJ;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClienteDTORequest (
    @NotBlank(message = "Nome é obrigatório")
    String nome,

    @ValidCNPJ
    String cnpj,

    @Email(message = "Email inválido")
    String email
){}

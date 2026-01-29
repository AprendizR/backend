package com.erp.transportadora.dto.response;

public record ClienteDTOResponse (
        Long id,
        String nome,
        String cnpj,
        String email
){}

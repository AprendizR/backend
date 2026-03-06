package com.erp.transportadora.dto.response;

public record ClienteDTOResponse (
        Long id,
        String nome,
        String email,
        String cidade,
        String endereco,
        String cep
){}

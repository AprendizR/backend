package com.erp.transportadora.dto.response;

public record ClienteDTOResponse (
        Long id,
        String nome,
        String cnpj,
        String cidade,
        String endereco,
        String cep
){}

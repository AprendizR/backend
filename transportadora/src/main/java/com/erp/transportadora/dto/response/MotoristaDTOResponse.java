package com.erp.transportadora.dto.response;

public record MotoristaDTOResponse (
    Long id,
    String nome,
    String apelido,
    String cpf,
    String telefone,
    Double valorDiaria
){}

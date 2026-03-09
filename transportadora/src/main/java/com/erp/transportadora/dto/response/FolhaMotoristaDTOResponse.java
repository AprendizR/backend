package com.erp.transportadora.dto.response;

public record FolhaMotoristaDTOResponse(
        Long motoristaId,
        String nome,
        String apelido,
        String cpf,
        String telefone,
        Integer diasComoMotorista,
        Integer diasComoAjudante,
        Double valorDiaria
) {}
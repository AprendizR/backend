package com.erp.transportadora.dto.response;

public record FolhaMotoristaDTOResponse(
        Long motoristaId,
        String nome,
        String apelido,
        String cpf,
        String telefone,
        Integer diasTrabalhados,
        Double valorDiaria,
        Double descontos,
        Double totalBruto,
        Double valorLiquido
) {}
package com.erp.transportadora.dto.response;

import java.util.List;
import java.time.LocalDateTime;

public record NotaFiscalDTOResponse(
        Long id,
        Long ordemServico,
        String numero,
        ClienteDTOResponse cliente,
        String remetente,
        String destinatario,
        String cep,
        String cidade,
        String endereco,
        Double frete,
        Double valor,
        Integer volumes,
        String status,
        List<String> fotos,
        Double latitude,
        Double longitude,
        String ultimoUsuarioAlteracao,
        LocalDateTime dataUltimaAlteracao
) {}

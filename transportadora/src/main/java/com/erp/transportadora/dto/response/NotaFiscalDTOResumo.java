package com.erp.transportadora.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record NotaFiscalDTOResumo(
        Long id,
        Long ordemServico,
        String numero,
        String destinatario,
        String cidade,
        String remetente,
        boolean entregue,
        List<String> fotos,
        Integer ordemEntrega,
        LocalDateTime dataEmissao,
        String ultimoUsuarioAlteracao,
        LocalDateTime dataUltimaAlteracao
) {}

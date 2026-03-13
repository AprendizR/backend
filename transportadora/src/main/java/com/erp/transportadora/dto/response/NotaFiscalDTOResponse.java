package com.erp.transportadora.dto.response;

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
        boolean temFoto,
        boolean isPdf
) {}
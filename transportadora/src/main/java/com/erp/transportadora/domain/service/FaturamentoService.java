package com.erp.transportadora.domain.service;

import com.erp.transportadora.domain.repository.NotaFiscalRepository;
import com.erp.transportadora.dto.response.FaturamentoDTOCidadeResponse;
import com.erp.transportadora.dto.response.FaturamentoDTOClienteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FaturamentoService {
    private final NotaFiscalRepository repository;

    public List<FaturamentoDTOClienteResponse> listar() {
        List<Object[]> resultados = repository.buscarFaturamentoPorRemetenteECidade();
        Map<String, List<FaturamentoDTOCidadeResponse>> agrupado = new LinkedHashMap<>();

        for (Object[] row : resultados) {
            String remetente = (String) row[0];
            String cidade = (String) row[1];
            Long total = (Long) row[2];

            agrupado.computeIfAbsent(remetente, k -> new ArrayList<>()).add(new FaturamentoDTOCidadeResponse(cidade, total));
        }

        return agrupado.entrySet().stream().map(e -> new FaturamentoDTOClienteResponse(
                e.getKey(),
                e.getValue().stream().mapToLong(FaturamentoDTOCidadeResponse::totalNotas).sum(),
                e.getValue()
        )).toList();
    }
}

package com.erp.transportadora.domain.service;

import com.erp.transportadora.domain.repository.NotaFiscalRepository;
import com.erp.transportadora.dto.response.FaturamentoDTOCidadeResponse;
import com.erp.transportadora.dto.response.FaturamentoDTOClienteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FaturamentoService {
    private final NotaFiscalRepository repository;

    public List<FaturamentoDTOClienteResponse> listar(LocalDate dataInicio, LocalDate dataFim) {

        LocalDateTime inicio = dataInicio != null ? dataInicio.atStartOfDay() : LocalDateTime.of(2000, 1, 1, 0, 0);
        LocalDateTime fim = dataFim != null ? dataFim.atTime(23, 59, 59) : LocalDateTime.of(2099, 12, 31, 23, 59);

        List<Object[]> resultados = repository.buscarFaturamentoPorClienteECidade(inicio, fim);
        Map<Long, List<Object[]>> agrupado = new LinkedHashMap<>();

        for (Object[] row : resultados) {
            Long clienteId = (Long) row[0];
            agrupado.computeIfAbsent(clienteId, k -> new ArrayList<>()).add(row);
        }

        return agrupado.entrySet().stream().map(e -> {
            List<Object[]> rows = e.getValue();
            String nomeCliente = (String) rows.get(0)[1];
            Long clienteId = e.getKey();

            List<FaturamentoDTOCidadeResponse> cidades = rows.stream().map(row ->
                    new FaturamentoDTOCidadeResponse(
                            (String) row[2],
                            (Long) row[3],
                            (Double) row[4]
                    )
            ).toList();

            Long totalNotas = cidades.stream().mapToLong(FaturamentoDTOCidadeResponse::totalNotas).sum();
            Double totalFrete = cidades.stream().mapToDouble(FaturamentoDTOCidadeResponse::totalFrete).sum();

            return new FaturamentoDTOClienteResponse(clienteId, nomeCliente, totalNotas, totalFrete, cidades);
        }).toList();
    }

    public List<Object[]> buscarNotasDoCliente(Long clienteId, LocalDate dataInicio, LocalDate dataFim) {
        LocalDateTime inicio = dataInicio != null ? dataInicio.atStartOfDay() : LocalDateTime.of(2000, 1, 1, 0, 0);
        LocalDateTime fim = dataFim != null ? dataFim.atTime(23, 59, 59) : LocalDateTime.of(2099, 12, 31, 23, 59);
        return repository.buscarNotasPorClienteEPeriodo(clienteId, inicio, fim);
    }
}

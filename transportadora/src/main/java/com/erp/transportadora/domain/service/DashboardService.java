package com.erp.transportadora.domain.service;

import com.erp.transportadora.domain.enums.Status;
import com.erp.transportadora.domain.repository.CargaRepository;
import com.erp.transportadora.domain.repository.NotaFiscalRepository;
import com.erp.transportadora.dto.response.EstatisticasDTOResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CargaRepository cargaRepository;
    private final NotaFiscalRepository notaFiscalRepository;

    public EstatisticasDTOResponse obterEstatisticas() {
        long totalCargas = cargaRepository.count();
        long cargasEmRota = cargaRepository.countByStatus(Status.EM_ROTA);
        long cargasEntregues = cargaRepository.countByStatus(Status.ENTREGUE);
        LocalDateTime inicioDoDia = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);

        long notasEntreguesHoje = notaFiscalRepository.countByEntregueAndDataEmissaoAfter(true, inicioDoDia);
        long totalNotas = notaFiscalRepository.count();
        long notasPendentes = notaFiscalRepository.countByEntregue(false);

        return new EstatisticasDTOResponse(
                totalCargas,
                cargasEmRota,
                cargasEntregues,
                notasEntreguesHoje,
                totalNotas,
                notasPendentes
        );
    }
}
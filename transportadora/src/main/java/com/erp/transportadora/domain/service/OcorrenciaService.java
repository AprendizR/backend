package com.erp.transportadora.domain.service;

import com.erp.transportadora.domain.entity.CargaEntity;
import com.erp.transportadora.domain.entity.NotaFiscalEntity;
import com.erp.transportadora.domain.entity.OcorrenciaEntity;
import com.erp.transportadora.domain.enums.StatusNota;
import com.erp.transportadora.domain.enums.SubtipoOcorrencia;
import com.erp.transportadora.domain.mapper.OcorrenciaMapper;
import com.erp.transportadora.domain.repository.NotaFiscalRepository;
import com.erp.transportadora.domain.repository.OcorrenciaRepository;
import com.erp.transportadora.dto.request.OcorrenciaDTORequest;
import com.erp.transportadora.dto.response.OcorrenciaDTOResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OcorrenciaService {

    private final OcorrenciaRepository ocorrenciaRepository;
    private final NotaFiscalRepository notaFiscalRepository;
    private final CargaService cargaService;

    @Transactional
    public OcorrenciaDTOResponse registrar(OcorrenciaDTORequest dto) {
        NotaFiscalEntity nota = notaFiscalRepository.findByOrdemServico(dto.ordemServico()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Nota fiscal com OS " + dto.ordemServico() + " não encontrada"));

        boolean jaFinalizada = nota.getStatus() != StatusNota.PENDENTE && nota.getStatus() != StatusNota.EM_ROTA;

        if (jaFinalizada) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nota já possui ocorrência de finalização registrada");
        }

        OcorrenciaEntity ocorrencia = new OcorrenciaEntity();
        ocorrencia.setNotaFiscal(nota);
        ocorrencia.setSubtipo(dto.subtipo());
        ocorrencia.setNomeRecebedor(dto.nomeRecebedor());
        ocorrencia.setObservacao(dto.observacao());
        ocorrencia.setUrlFotoComprovante(dto.urlFotoComprovante());
        ocorrencia.setDataOcorrencia(LocalDateTime.now());

        OcorrenciaEntity salva = ocorrenciaRepository.save(ocorrencia);

        StatusNota novoStatus = dto.subtipo().toStatusNota();
        nota.setStatus(novoStatus);
        notaFiscalRepository.save(nota);

        CargaEntity carga = nota.getCarga();
        if (carga != null) {
            cargaService.recalcularStatus(carga);
        }

        return OcorrenciaMapper.toResponse(salva);
    }

    // Buscar por ID da nota (uso interno)
    public List<OcorrenciaDTOResponse> listarPorNota(Long notaFiscalId) {
        return ocorrenciaRepository.findByNotaFiscalId(notaFiscalId)
                .stream()
                .map(OcorrenciaMapper::toResponse)
                .toList();
    }

    public List<OcorrenciaDTOResponse> listarPorOS(Long ordemServico) {
        return ocorrenciaRepository.findByNotaFiscalOrdemServico(ordemServico)
                .stream()
                .map(OcorrenciaMapper::toResponse)
                .toList();
    }
}
package com.erp.transportadora.domain.service;

import com.erp.transportadora.domain.entity.CargaEntity;
import com.erp.transportadora.domain.entity.MotoristaEntity;
import com.erp.transportadora.domain.entity.NotaFiscalEntity;
import com.erp.transportadora.domain.entity.VeiculoEntity;
import com.erp.transportadora.domain.enums.Status;
import com.erp.transportadora.domain.mapper.CargaMapper;
import com.erp.transportadora.domain.repository.CargaRepository;
import com.erp.transportadora.domain.repository.MotoristaRepository;
import com.erp.transportadora.domain.repository.NotaFiscalRepository;
import com.erp.transportadora.domain.repository.VeiculoRepository;
import com.erp.transportadora.dto.request.CargaDTORequest;
import com.erp.transportadora.dto.response.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CargaService {
    private final CargaRepository cargaRepository;
    private final VeiculoRepository veiculoRepository;
    private final MotoristaRepository motoristaRepository;
    private final NotaFiscalRepository notaFiscalRepository;

    @Transactional
    public CargaDTOResponse criar(CargaDTORequest dto) {
        VeiculoEntity veiculo = veiculoRepository.findById(dto.veiculoId()).orElseThrow(() -> new RuntimeException("Veículo não encontrado"));
        MotoristaEntity motorista = motoristaRepository.findById(dto.motoristaId()).orElseThrow(() -> new RuntimeException("Motorista não encontrado"));
        Long proximoNumero = cargaRepository.findMaxNumeroRota() + 1;
        CargaEntity carga = CargaEntity.criar(veiculo, motorista, proximoNumero);
        CargaEntity salva = cargaRepository.save(carga);

        return new CargaDTOResponse(
                salva.getId(),
                salva.getNumeroRota(),
                salva.getStatus(),
                veiculo.getId(),
                motorista.getId(),
                salva.getDataCriacao()

        );
    }

    @Transactional
    public void adicionarNota(Long cargaId, Long notaId) {

        CargaEntity carga = cargaRepository.findById(cargaId).orElseThrow(() -> new ResponseStatusException
                (HttpStatus.NOT_FOUND, "Carga não encontrada"));

        NotaFiscalEntity nota = notaFiscalRepository.findById(notaId).orElseThrow(() -> new ResponseStatusException
                (HttpStatus.NOT_FOUND, "Nota fiscal não encontrada"));

        if (nota.getCarga() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nota já vinculada a outra carga");
        }

        nota.setCarga(carga);
        carga.getNotasFiscais().add(nota);
        recalcularStatus(carga);
        notaFiscalRepository.save(nota);
        cargaRepository.save(carga);
    }

    @Transactional
    public void excluirNota(Long cargaId, Long notaId) {
        CargaEntity carga = cargaRepository.buscarComNotas(cargaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carga não encontrada"));

        NotaFiscalEntity nota = notaFiscalRepository.findById(notaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nota não encontrada"));

        if (nota.getCarga() == null || !nota.getCarga().getId().equals(cargaId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nota não pertence a esta carga");
        }

        nota.setCarga(null);
        carga.getNotasFiscais().remove(nota);
        recalcularStatus(carga);
        notaFiscalRepository.save(nota);
        cargaRepository.save(carga);
    }

    @Transactional
    public CargaDTODetalhada buscaDetalhada(Long id) {
        CargaEntity carga = cargaRepository.buscarComNotas(id).orElseThrow(() -> new ResponseStatusException
                (HttpStatus.NOT_FOUND, "Carga não encontrada"));
        return CargaMapper.toDetalhada(carga);
    }

    public void recalcularStatus(CargaEntity carga) {
        boolean algumaEntregue = false;
        boolean todasEntregues = true;
        for (NotaFiscalEntity nota : carga.getNotasFiscais()) {
            if (nota.getEntregue()) {
                algumaEntregue = true;
            } else {
                todasEntregues = false;
            }
        }
        if (carga.getNotasFiscais().isEmpty()) {
            carga.setStatus(Status.CENTRO_DISTRIBUICAO);
            return;
        }

        if (todasEntregues) {
            carga.setStatus(Status.ENTREGUE);
        } else if (algumaEntregue) {
            carga.setStatus(Status.EM_ROTA);
        } else {
            carga.setStatus(Status.CENTRO_DISTRIBUICAO);
        }
    }

    @Transactional
    public void iniciarRota(Long cargaId) {

        CargaEntity carga = cargaRepository.findById(cargaId).orElseThrow(() -> new RuntimeException("Carga não encontrada"));

        if (carga.getNotasFiscais().isEmpty()) {
            throw new RuntimeException("Carga sem notas fiscais");
        }
        if (carga.getStatus() != Status.CENTRO_DISTRIBUICAO) {
            throw new RuntimeException("Carga não pode iniciar rota");
        }
        carga.setStatus(Status.EM_ROTA);
        carga.setDataCarregamento(LocalDateTime.now());

        cargaRepository.save(carga);
    }

    public List<CargaDTOResumo> listar() {
        return cargaRepository.findAll().stream()
                .map(CargaMapper::toResumo)
                .toList();
    }

}

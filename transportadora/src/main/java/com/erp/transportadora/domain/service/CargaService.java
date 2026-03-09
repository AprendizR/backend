package com.erp.transportadora.domain.service;

import com.erp.transportadora.domain.entity.CargaEntity;
import com.erp.transportadora.domain.entity.MotoristaEntity;
import com.erp.transportadora.domain.entity.NotaFiscalEntity;
import com.erp.transportadora.domain.entity.VeiculoEntity;
import com.erp.transportadora.domain.enums.StatusCarga;
import com.erp.transportadora.domain.enums.StatusNota;
import com.erp.transportadora.domain.mapper.CargaMapper;
import com.erp.transportadora.domain.repository.CargaRepository;
import com.erp.transportadora.domain.repository.MotoristaRepository;
import com.erp.transportadora.domain.repository.NotaFiscalRepository;
import com.erp.transportadora.domain.repository.VeiculoRepository;
import com.erp.transportadora.domain.spec.CargaSpecification;
import com.erp.transportadora.dto.request.CargaDTORequest;
import com.erp.transportadora.dto.response.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
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

        CargaEntity carga = new CargaEntity();
        carga.setVeiculo(veiculo);
        carga.setMotorista(motorista);
        carga.setNumeroRota(proximoNumero);
        carga.setStatusCarga(StatusCarga.CENTRO_DISTRIBUICAO);
        carga.setDataCriacao(LocalDateTime.now());

        CargaEntity salva = cargaRepository.save(carga);

        motorista.setDiasTrabalhados(motorista.getDiasTrabalhados() + 1);
        motoristaRepository.save(motorista);

        return new CargaDTOResponse(
                salva.getId(),
                salva.getNumeroRota(),
                salva.getStatusCarga(),
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
        if (carga.getNotasFiscais().isEmpty()){
            carga.setStatusCarga(StatusCarga.CENTRO_DISTRIBUICAO);
            cargaRepository.save(carga);
            return;
        }

        boolean todasFinalizadas = carga.getNotasFiscais().stream().allMatch(n -> n.getStatus() != StatusNota.PENDENTE
        && n.getStatus() != StatusNota.EM_ROTA);
        boolean algumasFinalizadas = carga.getNotasFiscais().stream().anyMatch(n -> n.getStatus() != StatusNota.PENDENTE
        && n.getStatus() != StatusNota.EM_ROTA);

        if (todasFinalizadas){
            carga.setStatusCarga(StatusCarga.ENTREGUE);
        } else if (algumasFinalizadas) {
            carga.setStatusCarga(StatusCarga.EM_ROTA);
        } else {
            carga.setStatusCarga(StatusCarga.CENTRO_DISTRIBUICAO);
        }

        cargaRepository.save(carga);

    }

    public Page<CargaDTOResumo> listar(Long motoristaId, Long veiculoId, Long numeroCarga, LocalDate dataInicio, LocalDate dataFim, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dataCriacao").descending());
        Specification<CargaEntity> spec = CargaSpecification.filtrar(motoristaId, veiculoId, numeroCarga, dataInicio, dataFim);
        return cargaRepository.findAll(spec, pageable).map(CargaMapper::toResumo);
    }

}

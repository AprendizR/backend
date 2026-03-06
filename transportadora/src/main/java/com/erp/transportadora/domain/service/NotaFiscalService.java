package com.erp.transportadora.domain.service;

import com.erp.transportadora.domain.entity.NotaFiscalEntity;
import com.erp.transportadora.domain.enums.StatusNota;
import com.erp.transportadora.domain.mapper.NotaFiscalMapper;
import com.erp.transportadora.domain.repository.NotaFiscalRepository;
import com.erp.transportadora.domain.spec.NotaFiscalSpecification;
import com.erp.transportadora.dto.request.NotaFiscalDTORequest;
import com.erp.transportadora.dto.response.NotaFiscalDTOResponse;
import com.erp.transportadora.dto.response.NotaFiscalDTOResumo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class NotaFiscalService {
    private final NotaFiscalRepository repository;

    @Transactional
    public NotaFiscalDTOResponse criar(NotaFiscalDTORequest dto) {

        Long proximaOS = repository.findMaxOrdemServico() + 1;
        NotaFiscalEntity nota = NotaFiscalMapper.toEntity(dto);
        nota.setOrdemServico(proximaOS);
        NotaFiscalEntity salva = repository.save(nota);

        return NotaFiscalMapper.toResponse(salva);
    }

    public List<NotaFiscalDTOResponse> listar() {
        return repository.findAll().stream().map(NotaFiscalMapper::toResponse).toList();
    }

    // Buscar por ID (uso interno - adicionar nota em carga)
    public NotaFiscalEntity buscaPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Nota fiscal não encontrada"));
    }

    public List<NotaFiscalDTOResponse> listarDisponiveis() {
        return repository.findByCargaIsNullAndStatus(StatusNota.PENDENTE).stream().map(NotaFiscalMapper::toResponse).toList();
    }

    public Page<NotaFiscalDTOResponse> listar(String numero, Long ordemServico, String remetente, String destinatario, LocalDate dataInicio, LocalDate dataFim, int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("dataEmissao").descending());
        Specification<NotaFiscalEntity> spec = NotaFiscalSpecification.filtrar(numero, ordemServico, remetente, destinatario, dataInicio, dataFim);
        return repository.findAll(spec, pageable).map(NotaFiscalMapper::toResponse);
    }
}
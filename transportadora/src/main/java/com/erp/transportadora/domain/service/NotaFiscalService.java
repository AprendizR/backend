package com.erp.transportadora.domain.service;

import com.erp.transportadora.domain.entity.NotaFiscalEntity;
import com.erp.transportadora.domain.enums.StatusNota;
import com.erp.transportadora.domain.mapper.NotaFiscalMapper;
import com.erp.transportadora.domain.repository.NotaFiscalRepository;
import com.erp.transportadora.domain.spec.NotaFiscalSpecification;
import com.erp.transportadora.dto.request.NotaFiscalDTORequest;
import com.erp.transportadora.dto.response.NotaFiscalDTOResponse;
import com.erp.transportadora.dto.response.NotaFiscalDTOResumo;
import com.erp.transportadora.validators.NormalizadorUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class NotaFiscalService {
    private final NotaFiscalRepository repository;
    private final StorageService storageService;

    @Transactional
    public NotaFiscalDTOResponse criar(NotaFiscalDTORequest dto) {
        Long proximaOS = repository.findMaxOrdemServico() + 1;
        NotaFiscalEntity nota = NotaFiscalMapper.toEntity(dto);
        nota.setOrdemServico(proximaOS);
        NotaFiscalEntity salva = repository.save(nota);

        return NotaFiscalMapper.toResponse(salva);
    }

    public NotaFiscalEntity atualizar(Long id, NotaFiscalDTORequest dto) {
        NotaFiscalEntity entity = buscaPorId(id);
        entity.setNumero(NormalizadorUtils.apenasNumeros(dto.numero() != null ? dto.numero() : null));
        entity.setRemetente(NormalizadorUtils.trimUpper(dto.remetente() != null ? dto.remetente() : null));
        entity.setDestinatario(NormalizadorUtils.trimUpper(dto.destinatario() != null ? dto.destinatario() : null));
        entity.setCidade(NormalizadorUtils.trimUpper(dto.cidade() != null ? dto.cidade() : null));
        entity.setEndereco(NormalizadorUtils.trimUpper(dto.endereco() != null ? dto.endereco() : null));
        entity.setValor(dto.valor());
        entity.setVolumes(dto.volumes());
        entity.setCep(dto.cep() != null ? NormalizadorUtils.apenasNumeros(dto.cep()) : null);

        return repository.save(entity);
    }

    public List<NotaFiscalDTOResponse> listar() {
        return repository.findAll().stream().map(NotaFiscalMapper::toResponse).toList();
    }

    public NotaFiscalEntity buscaPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nota fiscal não encontrada"));
    }

    public List<NotaFiscalDTOResponse> listarDisponiveis() {
        return repository.findByCargaIsNullAndStatus(StatusNota.PENDENTE).stream().map(NotaFiscalMapper::toResponse).toList();
    }

    public Page<NotaFiscalDTOResponse> listar(String numero, Long ordemServico, String remetente, String destinatario, LocalDate dataInicio, LocalDate dataFim, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dataEmissao").descending());
        Specification<NotaFiscalEntity> spec = NotaFiscalSpecification.filtrar(numero, ordemServico, remetente, destinatario, dataInicio, dataFim);
        return repository.findAll(spec, pageable).map(NotaFiscalMapper::toResponse);
    }

    public void excluir(Long id) {
        buscaPorId(id);
        repository.deleteById(id);
    }

    @Transactional
    public void salvarFoto(Long id, MultipartFile arquivo) {
        NotaFiscalEntity nota = buscaPorId(id);

        // remove foto antiga se existir
        if (nota.getFotoComprovantePath() != null) {
            storageService.deletar(nota.getFotoComprovantePath());
        }

        String caminho = storageService.salvar(arquivo, "nota_" + id);
        nota.setFotoComprovantePath(caminho);
        repository.save(nota);
    }

    @Transactional
    public void removerFoto(Long id) {
        NotaFiscalEntity nota = buscaPorId(id);

        if (nota.getFotoComprovantePath() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhuma foto encontrada");
        }

        storageService.deletar(nota.getFotoComprovantePath());
        nota.setFotoComprovantePath(null);
        repository.save(nota);
    }
}
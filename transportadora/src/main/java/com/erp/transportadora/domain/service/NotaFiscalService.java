package com.erp.transportadora.domain.service;

import com.erp.transportadora.domain.entity.ClienteEntity;
import com.erp.transportadora.domain.entity.NotaFiscalEntity;
import com.erp.transportadora.domain.enums.StatusNota;
import com.erp.transportadora.domain.mapper.NotaFiscalMapper;
import com.erp.transportadora.domain.repository.ClienteRepository;
import com.erp.transportadora.domain.repository.NotaFiscalRepository;
import com.erp.transportadora.domain.spec.NotaFiscalSpecification;
import com.erp.transportadora.dto.request.NotaFiscalDTORequest;
import com.erp.transportadora.dto.response.NotaFiscalDTOResponse;
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
    private final CargaService cargaService;
    private final ClienteRepository clienteRepository;
    private final StorageService storageService;

    @Transactional
    public NotaFiscalDTOResponse criar(NotaFiscalDTORequest dto) {
        Long proximaOS = repository.findMaxOrdemServico() + 1;
        NotaFiscalEntity nota = NotaFiscalMapper.toEntity(dto);
        nota.setOrdemServico(proximaOS);
        if (dto.clienteId() != null) {
            ClienteEntity cliente = clienteRepository.findById(dto.clienteId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente nao encontrado"));
            nota.setCliente(cliente);
        }

        NotaFiscalEntity salva = repository.save(nota);
        return NotaFiscalMapper.toResponse(salva);
    }

    @Transactional
    public NotaFiscalDTOResponse atualizar(Long id, NotaFiscalDTORequest dto) {
        NotaFiscalEntity entity = repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nota não encontrada"));
        if (dto.clienteId() != null) {
            ClienteEntity cliente = clienteRepository.findById(dto.clienteId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
            entity.setCliente(cliente);
        } else {
            entity.setCliente(null);
        }
        entity.setNumero(NormalizadorUtils.apenasNumeros(dto.numero() != null ? dto.numero() : null));
        entity.setRemetente(NormalizadorUtils.trimUpper(dto.remetente() != null ? dto.remetente() : null));
        entity.setDestinatario(NormalizadorUtils.trimUpper(dto.destinatario() != null ? dto.destinatario() : null));
        entity.setCidade(NormalizadorUtils.trimUpper(dto.cidade() != null ? dto.cidade() : null));
        entity.setEndereco(NormalizadorUtils.trimUpper(dto.endereco() != null ? dto.endereco() : null));
        entity.setValor(dto.valor());
        entity.setVolumes(dto.volumes());
        entity.setCep(dto.cep() != null ? NormalizadorUtils.apenasNumeros(dto.cep()) : null);

        NotaFiscalEntity salva = repository.save(entity);

        return NotaFiscalMapper.toResponse(salva);
    }

    @Transactional
    public NotaFiscalDTOResponse buscaPorId(Long id) {
        return NotaFiscalMapper.toResponse(repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nota fiscal não encontrada")));
    }

    @Transactional
    public List<NotaFiscalDTOResponse> listarDisponiveis() {
        return repository.findByCargaIsNullAndStatus(StatusNota.PENDENTE)
                .stream()
                .map(NotaFiscalMapper::toResponse)
                .toList();
    }

    @Transactional
    public Page<NotaFiscalDTOResponse> listar(String numero, Long ordemServico, String remetente, String destinatario, LocalDate dataInicio, LocalDate dataFim, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dataEmissao").descending());
        Specification<NotaFiscalEntity> spec = NotaFiscalSpecification.filtrar(numero, ordemServico, remetente, destinatario, dataInicio, dataFim);
        return repository.findAll(spec, pageable).map(NotaFiscalMapper::toResponse);
    }

    @Transactional
    public void excluir(Long id) {
        buscaPorId(id);
        repository.deleteById(id);
    }

    @Transactional
    public void salvarFoto(Long id, MultipartFile arquivo) {
        NotaFiscalEntity nota = repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nota fiscal não encontrada"));
        String caminho = storageService.salvar(arquivo, "nota_" + id);
        nota.getFotos().add(caminho);
        repository.save(nota);
    }

    @Transactional
    public void removerFoto(Long id, String caminho) {
        NotaFiscalEntity nota = repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nota fiscal não encontrada"));
        storageService.deletar(caminho);
        nota.getFotos().remove(caminho);
        repository.save(nota);
    }

    @Transactional
    public void cancelarBaixa(Long id) {
        NotaFiscalEntity nota = repository.findById(id).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nota não encontrada"));
        if (nota.getStatus() != StatusNota.PENDENTE && nota.getStatus() != StatusNota.EM_ROTA) {
            nota.setStatus(StatusNota.PENDENTE);
        }
        repository.save(nota);

        if (nota.getCarga() != null) {
            cargaService.recalcularStatus(nota.getCarga());
        }
    }
}
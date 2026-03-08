package com.erp.transportadora.domain.service;

import com.erp.transportadora.domain.entity.MotoristaEntity;
import com.erp.transportadora.domain.mapper.MotoristaMapper;
import com.erp.transportadora.domain.repository.CargaRepository;
import com.erp.transportadora.domain.repository.MotoristaRepository;
import com.erp.transportadora.dto.request.MotoristaDTORequest;
import com.erp.transportadora.dto.response.MotoristaDTOResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MotoristaService {
    private final MotoristaRepository motoristaRepository;
    private final CargaRepository cargaRepository;

    public MotoristaEntity salvar(@Valid MotoristaEntity dto) {
        if (dto.getApelido() == null || dto.getApelido().isBlank()) {
            dto.setApelido(dto.getNome());
        }

        MotoristaEntity motorista = MotoristaMapper.toEntity(dto);

        motoristaRepository.findByCpf(motorista.getCpf()).ifPresent(m -> {
            throw new ResponseStatusException
                    (HttpStatus.CONFLICT, "CPF já cadastrado");
        });
        return motoristaRepository.save(motorista);
    }

    public MotoristaEntity atualizar(Long id, MotoristaDTORequest dto) {
        MotoristaEntity entity = buscaPorId(id);
        entity.setNome(dto.nome() != null ? dto.nome() : null);
        entity.setApelido(dto.apelido() != null && !dto.apelido().isBlank() ? dto.apelido() : entity.getNome());
        entity.setCpf(dto.cpf() != null ? dto.cpf() : null);
        entity.setTelefone(dto.telefone() != null ? dto.telefone() : null);

        return motoristaRepository.save(entity);
    }

    public MotoristaEntity buscaPorId(Long id) {
        return motoristaRepository.findById(id).orElseThrow(() -> new ResponseStatusException
                (HttpStatus.NOT_FOUND, "Motorista não encontrado"));
    }

    public List<MotoristaEntity> listarTodos() {
        return motoristaRepository.findAll();
    }

    public List<MotoristaDTOResponse> buscarPorNome(String nome) {
        return motoristaRepository.findByNomeContainingIgnoreCase(nome).stream().map(MotoristaMapper::toResponse).toList();
    }

    public void excluir(Long id) {
        buscaPorId(id);
        if (cargaRepository.existsByMotoristaId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Motorista possui em cargas");
        }
        motoristaRepository.deleteById(id);

    }
}
package com.erp.transportadora.domain.service;

import com.erp.transportadora.domain.entity.MotoristaEntity;
import com.erp.transportadora.domain.mapper.MotoristaMapper;
import com.erp.transportadora.domain.repository.MotoristaRepository;
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
    private final MotoristaRepository repository;


    public MotoristaEntity salvar(@Valid MotoristaEntity dto) {
        MotoristaEntity motorista = MotoristaMapper.toEntity(dto);

        repository.findByCpf(motorista.getCpf()).ifPresent(m -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado");
        });

        return repository.save(motorista);
    }

    public MotoristaEntity buscaPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Motorista não encontrado"));
    }

    public List<MotoristaEntity> listarTodos() {
        return repository.findAll();
    }

    public void desativar(Long id) {
        MotoristaEntity motorista = buscaPorId(id);
        motorista.setAtivo(false);
        repository.save(motorista);
    }

    public List<MotoristaDTOResponse> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(MotoristaMapper::toResponse)
                .toList();
    }
}
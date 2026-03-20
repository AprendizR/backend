package com.erp.transportadora.domain.service;

import com.erp.transportadora.domain.entity.ClienteEntity;
import com.erp.transportadora.domain.entity.MotoristaEntity;
import com.erp.transportadora.domain.mapper.ClienteMapper;
import com.erp.transportadora.domain.mapper.MotoristaMapper;
import com.erp.transportadora.domain.repository.CargaRepository;
import com.erp.transportadora.domain.repository.MotoristaRepository;
import com.erp.transportadora.dto.request.MotoristaDTORequest;
import com.erp.transportadora.dto.response.FolhaMotoristaDTOResponse;
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

    public MotoristaDTOResponse salvar(MotoristaDTORequest dto) {
        MotoristaEntity motorista = MotoristaMapper.toEntity(dto);

        if (motorista.getApelido() == null || motorista.getApelido().isBlank()) {
            motorista.setApelido(motorista.getNome());
        }

        return MotoristaMapper.toResponse(motoristaRepository.save(motorista));
    }

    public MotoristaDTOResponse atualizar(Long id, MotoristaDTORequest dto) {
        MotoristaEntity entity = motoristaRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Motorista não encontrado"));
        entity.setNome(dto.nome() != null ? dto.nome() : null);
        entity.setApelido(dto.apelido() != null && !dto.apelido().isBlank() ? dto.apelido() : entity.getNome());
        entity.setCpf(dto.cpf() != null ? dto.cpf() : null);
        entity.setTelefone(dto.telefone() != null ? dto.telefone() : null);
        entity.setValorDiaria(dto.valorDiaria() != null ? dto.valorDiaria() : null);

        return MotoristaMapper.toResponse(motoristaRepository.save(entity));
    }

    public MotoristaDTOResponse buscaPorId(Long id) {
        return MotoristaMapper.toResponse(motoristaRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Motorista não encontrado")));
    }

    public List<MotoristaDTOResponse> listarTodos() {
        return motoristaRepository.findAll()
                .stream()
                .map(MotoristaMapper::toResponse)
                .toList();
    }

    public List<MotoristaDTOResponse> buscarPorNome(String nome) {
        return motoristaRepository.findByNomeContainingIgnoreCaseOrApelidoContainingIgnoreCase(nome, nome)
                .stream().map(MotoristaMapper::toResponse).toList();
    }

    public void excluir(Long id) {
        buscaPorId(id);
        if (cargaRepository.existsByMotoristaId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Motorista possui em cargas");
        }
        motoristaRepository.deleteById(id);
    }

    public void zerarDias(Long id) {
        MotoristaEntity entity = motoristaRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Motorista não encontrado"));
        entity.setDiasComoAjudante(0);
        entity.setDiasComoMotorista(0);
        motoristaRepository.save(entity);
    }

    public FolhaMotoristaDTOResponse gerarFolha(Long id) {
        MotoristaEntity motorista = motoristaRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Motorista nao encontrado"));
        return new FolhaMotoristaDTOResponse(
                motorista.getId(),
                motorista.getNome(),
                motorista.getApelido(),
                motorista.getCpf(),
                motorista.getTelefone(),
                motorista.getDiasComoMotorista(),
                motorista.getDiasComoAjudante(),
                motorista.getValorDiaria()
        );
    }

    public MotoristaDTOResponse atualizarDescontos(Long id, Double descontos) {
        MotoristaEntity motorista = motoristaRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Motorista nao encontrado"));
        motorista.setDescontos(descontos);
        return MotoristaMapper.toResponse(motoristaRepository.save(motorista));
    }
}
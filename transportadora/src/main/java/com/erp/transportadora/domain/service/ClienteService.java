package com.erp.transportadora.domain.service;

import com.erp.transportadora.domain.entity.ClienteEntity;
import com.erp.transportadora.domain.mapper.ClienteMapper;
import com.erp.transportadora.domain.repository.ClienteRepository;
import com.erp.transportadora.dto.request.ClienteDTORequest;
import com.erp.transportadora.dto.response.ClienteDTOResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {
    private final ClienteRepository repository;

    public ClienteEntity salvar(ClienteDTORequest dto) {
        ClienteEntity cliente = ClienteMapper.toEntity(dto);
        if (cliente.getCnpj() != null) {
            repository.findByCnpj(cliente.getCnpj()).ifPresent(c -> {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "CNPJ já cadastrado");
            });
        }

        return repository.save(cliente);
    }

    public ClienteEntity buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    public List<ClienteDTOResponse> listarTodos() {
        return repository.findAll()
                .stream()
                .map(ClienteMapper::toResponse)
                .toList();
    }

}
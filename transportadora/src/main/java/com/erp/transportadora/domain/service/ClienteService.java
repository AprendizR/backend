package com.erp.transportadora.domain.service;

import com.erp.transportadora.domain.entity.ClienteEntity;
import com.erp.transportadora.domain.mapper.ClienteMapper;
import com.erp.transportadora.domain.repository.ClienteRepository;
import com.erp.transportadora.dto.request.ClienteDTORequest;
import com.erp.transportadora.dto.response.ClienteDTOResponse;
import com.erp.transportadora.validators.NormalizadorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {
    private final ClienteRepository repository;

    public ClienteDTOResponse salvar(ClienteDTORequest dto) {
        ClienteEntity cliente = ClienteMapper.toEntity(dto);
        if (cliente.getCnpj() != null) {
            repository.findByCnpj(cliente.getCnpj()).ifPresent(c -> {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "CNPJ já cadastrado");
            });
        }
        return ClienteMapper.toResponse(repository.save(cliente));
    }

    public ClienteDTOResponse atualizar(Long id, ClienteDTORequest dto) {
        ClienteEntity entity = repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente nao encontrado"));
        entity.setNome(NormalizadorUtils.trimUpper(dto.nome()));
        entity.setCnpj(dto.cnpj() != null ? NormalizadorUtils.apenasNumeros(dto.cnpj()) : null);
        entity.setCidade(dto.cidade() != null ? NormalizadorUtils.trimUpper(dto.cidade()) : null);
        entity.setEndereco(dto.endereco() != null ? NormalizadorUtils.trimUpper(dto.endereco()) : null);
        entity.setCep(dto.cep() != null ? NormalizadorUtils.apenasNumeros(dto.cep()) : null);
        return ClienteMapper.toResponse(repository.save(entity));
    }

    public List<ClienteDTOResponse> listarTodos() {
        return repository.findAll()
                .stream()
                .map(ClienteMapper::toResponse)
                .toList();
    }

    public ClienteDTOResponse buscarPorId(Long id) {
        return ClienteMapper.toResponse(repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente ID não encontrado")));
    }

    public void deletar(Long id) {
        repository.findById(id);
        repository.deleteById(id);
    }

}
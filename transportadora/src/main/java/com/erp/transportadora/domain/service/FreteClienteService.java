package com.erp.transportadora.domain.service;

import com.erp.transportadora.domain.entity.ClienteEntity;
import com.erp.transportadora.domain.entity.FreteClienteEntity;
import com.erp.transportadora.domain.repository.ClienteRepository;
import com.erp.transportadora.domain.repository.FreteClienteRepository;
import com.erp.transportadora.dto.request.FreteClienteDTORequest;
import com.erp.transportadora.dto.response.FreteClienteDTOResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FreteClienteService {

    private final FreteClienteRepository freteRepository;
    private final ClienteRepository clienteRepository;

    public List<FreteClienteDTOResponse> listarPorCliente(Long clienteId) {
        return freteRepository.findByClienteId(clienteId).stream().map(this::toResponse).toList();
    }

    public Optional<FreteClienteDTOResponse> buscar(Long clienteId, String cidade) {
        return freteRepository.findByClienteIdAndCidadeIgnoreCase(clienteId, cidade).map(this::toResponse);
    }

    public FreteClienteDTOResponse salvar(FreteClienteDTORequest dto) {
        ClienteEntity cliente = clienteRepository.findById(dto.clienteId()).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        FreteClienteEntity frete = freteRepository.findByClienteIdAndCidadeIgnoreCase(dto.clienteId(), dto.cidade())
                .orElse(FreteClienteEntity.builder().cliente(cliente).build());

        frete.setCidade(dto.cidade().toUpperCase().trim());
        frete.setValor(dto.valor());
        return toResponse(freteRepository.save(frete));
    }

    public void deletar(Long id) {
        if (!freteRepository.existsById(id)) throw new ResponseStatusException
                (HttpStatus.NOT_FOUND, "Frete não encontrado");
        freteRepository.deleteById(id);
    }

    private FreteClienteDTOResponse toResponse(FreteClienteEntity e) {
        return new FreteClienteDTOResponse(e.getId(), e.getCliente().getId(), e.getCidade(), e.getValor());
    }
}
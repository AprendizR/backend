package com.erp.transportadora.domain.service;

import com.erp.transportadora.domain.entity.VeiculoEntity;
import com.erp.transportadora.domain.mapper.VeiculoMapper;
import com.erp.transportadora.domain.repository.VeiculoRepository;
import com.erp.transportadora.dto.request.VeiculoDTORequest;
import com.erp.transportadora.dto.response.VeiculoDTOResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@AllArgsConstructor
public class VeiculoService {
    private final VeiculoRepository repository;

    public VeiculoDTOResponse salvar(VeiculoDTORequest dto) {
        VeiculoEntity veiculo = VeiculoMapper.toEntity(dto);
        if (repository.existsByPlaca(dto.placa())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Placa já existente");
        }
        return VeiculoMapper.toResponse(repository.save(veiculo));
    }

    public VeiculoEntity atualizar(Long id, VeiculoDTORequest dto) {
        VeiculoEntity entity = buscarPorId(id);
        entity.setPlaca(dto.placa() != null ? dto.placa() : null);
        entity.setModelo(dto.modelo() != null ? dto.modelo() : null);

        return repository.save(entity);
    }

    public VeiculoEntity buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veiculo não encontrado"));
    }

    public List<VeiculoDTOResponse> listarTodos() {
        return repository.findAll()
                .stream()
                .map(VeiculoMapper::toResponse)
                .toList();
    }

    public List<VeiculoDTOResponse> buscarPorPlaca(String placa) {
        return repository.findByPlacaContainingIgnoreCase(placa).stream().map(VeiculoMapper::toResponse).toList();
    }

    public void excluir(Long id) {
        repository.findById(id);
        repository.deleteById(id);
    }

}

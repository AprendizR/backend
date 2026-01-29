package com.erp.transportadora.domain.service;

import com.erp.transportadora.domain.entity.VeiculoEntity;
import com.erp.transportadora.domain.mapper.MotoristaMapper;
import com.erp.transportadora.domain.mapper.VeiculoMapper;
import com.erp.transportadora.domain.repository.VeiculoRepository;
import com.erp.transportadora.dto.response.MotoristaDTOResponse;
import com.erp.transportadora.dto.response.VeiculoDTOResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class VeiculoService {
    private final VeiculoRepository repository;

    public VeiculoEntity salvar(VeiculoEntity veiculo){
        if (repository.existsByPlaca(veiculo.getPlaca())){
            throw new RuntimeException("Erro: Já existe esse veículo cadastrado" + veiculo.getPlaca());
        }
        return repository.save(veiculo);
    }

    public VeiculoEntity buscarPorId(Long id){
        return repository.findById(id).orElseThrow(()-> new RuntimeException("Veiculo não encontrado"));
    }

    public List<VeiculoDTOResponse> listarTodos() {
        return repository.findAll()
                .stream()
                .map(VeiculoMapper::toResponse)
                .toList();
    }

    public List<VeiculoDTOResponse> buscarPorPlaca(String placa){
        return repository.findByPlacaContainingIgnoreCase(placa).stream().map(VeiculoMapper::toResponse).toList();
    }

}

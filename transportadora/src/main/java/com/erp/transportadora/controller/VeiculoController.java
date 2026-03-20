package com.erp.transportadora.controller;

import com.erp.transportadora.domain.entity.VeiculoEntity;
import com.erp.transportadora.domain.service.VeiculoService;
import com.erp.transportadora.dto.request.VeiculoDTORequest;
import com.erp.transportadora.dto.response.VeiculoDTOResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/veiculos")
@RequiredArgsConstructor
public class VeiculoController {
    private final VeiculoService service;

    @PostMapping
    public ResponseEntity<VeiculoDTOResponse> criar(@RequestBody VeiculoDTORequest veiculo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(veiculo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VeiculoEntity> atualizarVeiculo(@PathVariable Long id, @Valid @RequestBody VeiculoDTORequest dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @GetMapping
    public List<VeiculoDTOResponse> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/buscar")
    public List<VeiculoDTOResponse> buscarPorPlaca(@RequestParam(required = false) String placa) {
        if (placa == null || placa.isBlank()) {
            return List.of();
        }
        return service.buscarPorPlaca(placa);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VeiculoEntity> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirVeiculo(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

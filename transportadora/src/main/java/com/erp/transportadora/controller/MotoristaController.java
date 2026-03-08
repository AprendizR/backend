package com.erp.transportadora.controller;

import com.erp.transportadora.domain.entity.MotoristaEntity;
import com.erp.transportadora.domain.service.MotoristaService;
import com.erp.transportadora.dto.request.MotoristaDTORequest;
import com.erp.transportadora.dto.response.MotoristaDTOResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/motoristas")
@RequiredArgsConstructor
public class MotoristaController {
    private final MotoristaService service;

    @PostMapping
    public ResponseEntity<MotoristaEntity> criar(@Valid @RequestBody MotoristaEntity motorista) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(motorista));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MotoristaEntity> atualizar(@PathVariable Long id, @Valid @RequestBody MotoristaDTORequest dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @GetMapping
    public List<MotoristaEntity> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MotoristaEntity> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscaPorId(id));
    }

    @GetMapping("/buscar")
    public List<MotoristaDTOResponse> buscarPorNome(@RequestParam(required = false) String nome) {
        if (nome == null || nome.isBlank()) {
            return List.of();
        }
        return service.buscarPorNome(nome);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MotoristaEntity> deletar(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

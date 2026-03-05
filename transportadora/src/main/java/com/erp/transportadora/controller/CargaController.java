package com.erp.transportadora.controller;


import com.erp.transportadora.domain.service.CargaService;
import com.erp.transportadora.dto.request.CargaDTORequest;
import com.erp.transportadora.dto.response.CargaDTOResponse;
import com.erp.transportadora.dto.response.CargaDTODetalhada;
import com.erp.transportadora.dto.response.CargaDTOResumo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/cargas")
@RequiredArgsConstructor

public class CargaController {
    private final CargaService service;

    @PostMapping
    public ResponseEntity<CargaDTOResponse> criar(@RequestBody CargaDTORequest dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PostMapping("/{cargaId}/notas/{notaId}")
    public ResponseEntity<Void> adicionarNota(@PathVariable Long cargaId, @PathVariable Long notaId) {
        service.adicionarNota(cargaId, notaId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cargaId}/notas/{notaId}")
    public ResponseEntity<Void> excluirNota(@PathVariable Long cargaId, @PathVariable Long notaId) {
        service.excluirNota(cargaId, notaId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CargaDTODetalhada> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscaDetalhada(id));
    }

    @GetMapping
    public ResponseEntity<Page<CargaDTOResumo>> listar(
            @RequestParam(required = false) Long motoristaId,
            @RequestParam(required = false) Long veiculoId,
            @RequestParam(required = false) Long numeroCarga,
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(service.listar(motoristaId, veiculoId, numeroCarga, dataInicio, dataFim, page, size));
    }

}

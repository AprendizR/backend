package com.erp.transportadora.controller;


import com.erp.transportadora.domain.service.CargaService;
import com.erp.transportadora.dto.request.CargaDTORequest;
import com.erp.transportadora.dto.response.CargaDTOResponse;
import com.erp.transportadora.dto.response.CargaDTODetalhada;
import com.erp.transportadora.dto.response.CargaDTOResumo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}")
    public ResponseEntity<CargaDTODetalhada> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarDetalhada(id));
    }

    @PostMapping("/{id}/iniciar-rota")
    public void iniciarRota(@PathVariable Long id) {
        service.iniciarRota(id);
    }

    @GetMapping
    public List<CargaDTOResumo> listar() {
        return service.listar();
    }



}

package com.erp.transportadora.controller;

import com.erp.transportadora.domain.service.OcorrenciaService;
import com.erp.transportadora.dto.request.OcorrenciaDTORequest;
import com.erp.transportadora.dto.response.OcorrenciaDTOResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/ocorrencias")
@RequiredArgsConstructor
public class OcorrenciaController {

    private final OcorrenciaService service;

    @PostMapping
    public ResponseEntity<OcorrenciaDTOResponse> registrar(@RequestBody OcorrenciaDTORequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(dto));
    }

    // Manter por ID interno
    @GetMapping("/nota/{notaId}")
    public List<OcorrenciaDTOResponse> listarPorNota(@PathVariable Long notaId) {
        return service.listarPorNota(notaId);
    }

    // NOVO - Listar por OS
    @GetMapping("/os/{ordemServico}")
    public List<OcorrenciaDTOResponse> listarPorOS(@PathVariable Long ordemServico) {
        return service.listarPorOS(ordemServico);
    }
}
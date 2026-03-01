package com.erp.transportadora.controller;

import com.erp.transportadora.domain.entity.NotaFiscalEntity;
import com.erp.transportadora.domain.service.NotaFiscalService;
import com.erp.transportadora.dto.request.NotaFiscalDTORequest;
import com.erp.transportadora.dto.response.NotaFiscalDTOResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/notas-fiscais")
@RequiredArgsConstructor
public class NotaFiscalController {
    private final NotaFiscalService service;

    @PostMapping
    public ResponseEntity<NotaFiscalDTOResponse> criar(@RequestBody NotaFiscalDTORequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<NotaFiscalDTOResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    // Buscar por ID (uso interno)
    @GetMapping("/{id}")
    public ResponseEntity<NotaFiscalEntity> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscaPorId(id));
    }

    @GetMapping("/os/{ordemServico}")
    public ResponseEntity<NotaFiscalEntity> buscarPorOS(@PathVariable Long ordemServico) {
        return ResponseEntity.ok(service.buscarPorOS(ordemServico));
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<List<NotaFiscalDTOResponse>> listarDisponiveis() {
        return ResponseEntity.ok(service.listarDisponiveis());
    }
}
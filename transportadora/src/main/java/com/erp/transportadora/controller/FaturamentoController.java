package com.erp.transportadora.controller;

import com.erp.transportadora.domain.service.FaturamentoService;
import com.erp.transportadora.dto.response.FaturamentoDTOClienteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/faturamento")
@RequiredArgsConstructor
public class FaturamentoController {
    private final FaturamentoService service;

    @GetMapping
    public ResponseEntity<List<FaturamentoDTOClienteResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }
}
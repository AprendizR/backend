package com.erp.transportadora.controller;

import com.erp.transportadora.domain.service.FaturamentoService;
import com.erp.transportadora.dto.response.FaturamentoDTOClienteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/faturamento")
@RequiredArgsConstructor
public class FaturamentoController {
    private final FaturamentoService service;

    @GetMapping
    public ResponseEntity<List<FaturamentoDTOClienteResponse>> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return ResponseEntity.ok(service.listar(dataInicio, dataFim));
    }
}
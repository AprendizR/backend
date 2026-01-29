package com.erp.transportadora.controller;

import com.erp.transportadora.domain.service.DashboardService;
import com.erp.transportadora.dto.response.EstatisticasDTOResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService service;

    @GetMapping("/estatisticas")
    public ResponseEntity<EstatisticasDTOResponse> obterEstatisticas() {
        return ResponseEntity.ok(service.obterEstatisticas());
    }
}
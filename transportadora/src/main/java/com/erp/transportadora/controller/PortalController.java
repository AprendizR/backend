package com.erp.transportadora.controller;

import com.erp.transportadora.domain.service.PortalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/portal")
@RequiredArgsConstructor
public class PortalController {

    private final PortalService service;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(service.login(body.get("cnpj"), body.get("senha")));
    }

    @GetMapping("/notas")
    public ResponseEntity<?> buscarNotas(
            @RequestParam String cnpj,
            @RequestParam(required = false) String numero,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim) {
        return ResponseEntity.ok(service.buscarNotas(cnpj, numero, status, dataInicio, dataFim));
    }
}
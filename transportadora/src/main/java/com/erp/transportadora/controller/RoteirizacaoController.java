package com.erp.transportadora.controller;

import com.erp.transportadora.domain.service.RoteirizacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/cargas")
@RequiredArgsConstructor
public class RoteirizacaoController {

    private final RoteirizacaoService service;

    @PostMapping("/{id}/roteirizar")
    public ResponseEntity<Void> roteirizar(@PathVariable Long id) {
        service.roteirizar(id);
        return ResponseEntity.ok().build();
    }
}
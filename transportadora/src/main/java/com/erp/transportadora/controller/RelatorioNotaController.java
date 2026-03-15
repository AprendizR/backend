package com.erp.transportadora.controller;

import com.erp.transportadora.domain.service.RelatorioNotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/notas-fiscais")
@RequiredArgsConstructor
public class RelatorioNotaController {

    private final RelatorioNotaService service;

    @GetMapping("/{id}/relatorio")
    public ResponseEntity<byte[]> gerarRelatorio(@PathVariable Long id) {
        byte[] arquivo = service.gerarRelatorio(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=comprovante_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(arquivo);
    }
}
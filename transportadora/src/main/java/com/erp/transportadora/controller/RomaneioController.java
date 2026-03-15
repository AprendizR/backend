package com.erp.transportadora.controller;

import com.erp.transportadora.domain.service.RomaneioExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/cargas")
@RequiredArgsConstructor
public class RomaneioController {

    private final RomaneioExcelService service;

    @GetMapping("/{id}/romaneio")
    public ResponseEntity<byte[]> gerarRomaneio(@PathVariable Long id) {
        byte[] arquivo = service.gerarRomaneio(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=romaneio_" + id + ".xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(arquivo);
    }
}
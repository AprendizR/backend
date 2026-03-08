package com.erp.transportadora.controller;

import com.erp.transportadora.domain.entity.NotaFiscalEntity;
import com.erp.transportadora.domain.service.NotaFiscalService;
import com.erp.transportadora.dto.request.NotaFiscalDTORequest;
import com.erp.transportadora.dto.response.NotaFiscalDTOResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @PutMapping("/{id}")
    public ResponseEntity<NotaFiscalEntity> atualizar(@PathVariable Long id, @RequestBody NotaFiscalDTORequest dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @GetMapping
    public ResponseEntity<Page<NotaFiscalDTOResponse>> listar(
            @RequestParam(required = false) String numero,
            @RequestParam(required = false) Long ordemServico,
            @RequestParam(required = false) String remetente,
            @RequestParam(required = false) String destinatario,
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.listar(numero, ordemServico, remetente, destinatario, dataInicio, dataFim, page, size));
    }

    // Buscar por ID (uso interno)
    @GetMapping("/{id}")
    public ResponseEntity<NotaFiscalEntity> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscaPorId(id));
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<List<NotaFiscalDTOResponse>> listarDisponiveis() {
        return ResponseEntity.ok(service.listarDisponiveis());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id){
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
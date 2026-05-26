package com.erp.transportadora.controller;

import com.erp.transportadora.domain.service.FreteClienteService;
import com.erp.transportadora.dto.request.FreteClienteDTORequest;
import com.erp.transportadora.dto.response.FreteClienteDTOResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/fretes")
@RequiredArgsConstructor
public class FreteClienteController {

    private final FreteClienteService service;

    @GetMapping
    public ResponseEntity<?> buscar(@RequestParam Long clienteId, @RequestParam(required = false) String cidade) {
        if (cidade != null && !cidade.isBlank()) {
            return ResponseEntity.ok(service.buscar(clienteId, cidade));
        }
        return ResponseEntity.ok(service.listarPorCliente(clienteId));
    }

    @PostMapping
    public ResponseEntity<FreteClienteDTOResponse> salvar(@RequestBody FreteClienteDTORequest dto) {
        return ResponseEntity.ok(service.salvar(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
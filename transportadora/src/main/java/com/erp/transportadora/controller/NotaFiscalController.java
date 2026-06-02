package com.erp.transportadora.controller;

import com.erp.transportadora.domain.service.NotaFiscalService;
import com.erp.transportadora.dto.request.NotaFiscalDTORequest;
import com.erp.transportadora.dto.response.NotaFiscalDTOResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/notas-fiscais")
@RequiredArgsConstructor
public class NotaFiscalController {
    private final NotaFiscalService service;

    @PostMapping
    public ResponseEntity<NotaFiscalDTOResponse> criar(@RequestBody NotaFiscalDTORequest dto, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto, usuarioLogado(authentication)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotaFiscalDTOResponse> atualizar(@PathVariable Long id, @RequestBody NotaFiscalDTORequest dto, Authentication authentication) {
        return ResponseEntity.ok(service.atualizar(id, dto, usuarioLogado(authentication)));
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
    public ResponseEntity<NotaFiscalDTOResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscaPorId(id));
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<List<NotaFiscalDTOResponse>> listarDisponiveis() {
        return ResponseEntity.ok(service.listarDisponiveis());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/foto")
    public ResponseEntity<Void> uploadFoto(@PathVariable Long id, @RequestParam("arquivo") MultipartFile arquivo, Authentication authentication) {
        service.salvarFoto(id, arquivo, usuarioLogado(authentication));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/foto")
    public ResponseEntity<Void> removerFoto(@PathVariable Long id, @RequestParam String caminho, Authentication authentication) {
        service.removerFoto(id, caminho, usuarioLogado(authentication));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/foto")
    public ResponseEntity<Resource> buscarFoto(@PathVariable Long id, @RequestParam String caminho) {
        try {
            Path path = Paths.get("uploads").resolve(caminho);
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists()) return ResponseEntity.notFound().build();
            String contentType = Files.probeContentType(path);
            if (contentType == null) contentType = "application/octet-stream";
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}/cancelar-baixa")
    public ResponseEntity<Void> cancelarBaixa(@PathVariable Long id, Authentication authentication) {
        service.cancelarBaixa(id, usuarioLogado(authentication));
        return ResponseEntity.ok().build();
    }

    private String usuarioLogado(Authentication authentication) {
        return authentication != null ? authentication.getName() : null;
    }
}

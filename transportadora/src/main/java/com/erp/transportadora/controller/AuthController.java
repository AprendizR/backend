package com.erp.transportadora.controller;

import com.erp.transportadora.domain.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> body) {
        String token = service.login(body.get("nome"), body.get("senha"));
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/registrar")
    public ResponseEntity<Void> registrar(@RequestBody Map<String, String> body) {
        service.registrar(body.get("nome"), body.get("senha"));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<Map<String, Object>>> listarUsuarios() {
        return ResponseEntity.ok(service.listarUsuarios());
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id, Authentication authentication) {
        String nomeLogado = authentication.getName();
        if (!nomeLogado.equals("admin")){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas administrador pode excluir");
        }
        service.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
package com.erp.transportadora.domain.service;

import com.erp.transportadora.domain.entity.UsuarioEntity;
import com.erp.transportadora.domain.repository.UsuarioRepository;
import com.erp.transportadora.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository repository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public String login(String nome, String senha) {
        UsuarioEntity usuario = repository.findByNome(nome)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado"));

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Senha incorreta");
        }

        return jwtService.gerarToken(usuario.getNome());
    }

    public void registrar(String nome, String senha) {
        if (repository.findByNome(nome).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário já existe");
        }

        UsuarioEntity usuario = UsuarioEntity.builder()
                .nome(nome)
                .senha(passwordEncoder.encode(senha))
                .build();

        repository.save(usuario);
    }

    public List<Map<String, Object>> listarUsuarios() {
        return repository.findAll().stream()
                .map(u -> Map.of("id", (Object) u.getId(), "nome", u.getNome()))
                .toList();
    }

    public void deletarUsuario(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }
        repository.deleteById(id);
    }
}
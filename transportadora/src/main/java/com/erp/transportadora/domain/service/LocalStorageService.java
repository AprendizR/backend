package com.erp.transportadora.domain.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class LocalStorageService implements StorageService {

    @Value("${storage.local.path:uploads}")
    private String basePath;

    @Override
    public String salvar(MultipartFile arquivo, String prefixo) {
        try {
            Path dir = Paths.get(basePath);
            Files.createDirectories(dir);

            String extensao = obterExtensao(arquivo.getOriginalFilename());
            String nomeArquivo = prefixo + "_" + UUID.randomUUID() + extensao;
            Path destino = dir.resolve(nomeArquivo);

            Files.copy(arquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

            return nomeArquivo;
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar arquivo", e);
        }
    }

    @Override
    public void deletar(String nomeArquivo) {
        try {
            Path arquivo = Paths.get(basePath).resolve(nomeArquivo);
            Files.deleteIfExists(arquivo);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao deletar arquivo", e);
        }
    }

    private String obterExtensao(String nomeOriginal) {
        if (nomeOriginal == null || !nomeOriginal.contains(".")) return ".jpg";
        return nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
    }
}
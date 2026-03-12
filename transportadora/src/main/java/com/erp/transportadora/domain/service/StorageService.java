package com.erp.transportadora.domain.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String salvar(MultipartFile arquivo, String prefixo);
    void deletar(String caminho);
}
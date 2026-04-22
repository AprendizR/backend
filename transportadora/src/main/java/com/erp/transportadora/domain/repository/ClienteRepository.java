package com.erp.transportadora.domain.repository;

import com.erp.transportadora.domain.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<ClienteEntity, Long> {
    Optional<ClienteEntity> findByCnpj(String cnpj);

}
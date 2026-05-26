package com.erp.transportadora.domain.repository;

import com.erp.transportadora.domain.entity.FreteClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FreteClienteRepository extends JpaRepository<FreteClienteEntity, Long> {
    List<FreteClienteEntity> findByClienteId(Long clienteId);
    Optional<FreteClienteEntity> findByClienteIdAndCidadeIgnoreCase(Long clienteId, String cidade);
}
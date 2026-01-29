package com.erp.transportadora.domain.repository;

import com.erp.transportadora.domain.entity.MotoristaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MotoristaRepository extends JpaRepository<MotoristaEntity, Long> {
    Optional<MotoristaEntity> findByCpf(String cpf);

    List<MotoristaEntity> findByNomeContainingIgnoreCase(String nome);
}


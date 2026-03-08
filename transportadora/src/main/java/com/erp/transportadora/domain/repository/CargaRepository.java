package com.erp.transportadora.domain.repository;

import com.erp.transportadora.domain.entity.CargaEntity;
import com.erp.transportadora.domain.enums.StatusCarga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CargaRepository extends JpaRepository<CargaEntity, Long>, JpaSpecificationExecutor<CargaEntity> {
    @Query("SELECT c FROM CargaEntity c LEFT JOIN FETCH c.notasFiscais WHERE c.id = :id")
    Optional<CargaEntity> buscarComNotas(@Param("id") Long id);

    @Query("SELECT coalesce(max(c.numeroRota), 0) FROM CargaEntity c")
    Long findMaxNumeroRota();

    long countByStatusCarga(StatusCarga statusCarga);

    boolean existsByMotoristaId(Long motoristaId);
}


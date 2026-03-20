package com.erp.transportadora.domain.repository;

import com.erp.transportadora.domain.entity.CargaEntity;
import com.erp.transportadora.domain.enums.StatusCarga;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CargaRepository extends JpaRepository<CargaEntity, Long>, JpaSpecificationExecutor<CargaEntity> {
    @QueryHints(@QueryHint(name = "jakarta.persistence.cache.retrieveMode", value = "BYPASS"))
    @Query("SELECT DISTINCT c FROM CargaEntity c LEFT JOIN FETCH c.notasFiscais WHERE c.id = :id")
    Optional<CargaEntity> buscarComNotas(@Param("id") Long id);

    @Query("SELECT coalesce(max(c.numeroRota), 0) FROM CargaEntity c")
    Long findMaxNumeroRota();

    long countByStatusCarga(StatusCarga statusCarga);

    boolean existsByMotoristaId(Long motoristaId);

    @Query("SELECT c FROM CargaEntity c WHERE c.motorista.id = :motoristaId AND c.dataCriacao BETWEEN :inicio AND :fim")
    List<CargaEntity> buscarPorMotoristaEPeriodo(
            @Param("motoristaId") Long motoristaId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );

    @Query("SELECT c FROM CargaEntity c WHERE c.ajudante.id = :ajudanteId AND c.dataCriacao BETWEEN :inicio AND :fim")
    List<CargaEntity> buscarPorAjudanteEPeriodo(
            @Param("ajudanteId") Long ajudanteId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );
}


package com.erp.transportadora.domain.repository;

import com.erp.transportadora.domain.entity.OcorrenciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OcorrenciaRepository extends JpaRepository<OcorrenciaEntity, Long> {

    // Buscar por ID da nota (uso interno)
    List<OcorrenciaEntity> findByNotaFiscalId(Long notaFiscalId);

    // ⭐ NOVO - Buscar por OS da nota (usando relacionamento)
    List<OcorrenciaEntity> findByNotaFiscalOrdemServico(Long ordemServico);

    // OU usando @Query (mais explícito)
    @Query("SELECT o FROM OcorrenciaEntity o WHERE o.notaFiscal.ordemServico = :ordemServico")
    List<OcorrenciaEntity> buscarPorOS(@Param("ordemServico") Long ordemServico);
}
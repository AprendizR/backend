package com.erp.transportadora.domain.repository;

import com.erp.transportadora.domain.entity.OcorrenciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OcorrenciaRepository extends JpaRepository<OcorrenciaEntity, Long> {

    List<OcorrenciaEntity> findByNotaFiscalId(Long notaFiscalId);

    List<OcorrenciaEntity> findByNotaFiscalOrdemServico(Long ordemServico);

    @Query("SELECT o FROM OcorrenciaEntity o WHERE o.notaFiscal.ordemServico = :ordemServico")
    List<OcorrenciaEntity> buscarPorOS(@Param("ordemServico") Long ordemServico);
}
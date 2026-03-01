package com.erp.transportadora.domain.repository;

import com.erp.transportadora.domain.entity.NotaFiscalEntity;
import com.erp.transportadora.domain.enums.StatusNota;
import org.aspectj.weaver.ast.Not;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotaFiscalRepository extends JpaRepository<NotaFiscalEntity, Long> {

    Optional<NotaFiscalEntity> findByOrdemServico(Long ordemServico);

    @Query("SELECT COALESCE(MAX(n.ordemServico), 0) FROM NotaFiscalEntity n")
    Long findMaxOrdemServico();

    long countByStatus(StatusNota status);

    long countByStatusAndDataEmissaoAfter(StatusNota status, LocalDateTime data);

    List<NotaFiscalEntity> findByCargaIsNullAndStatus(StatusNota status);
}
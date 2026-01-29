package com.erp.transportadora.domain.repository;

import com.erp.transportadora.domain.entity.NotaFiscalEntity;
import org.aspectj.weaver.ast.Not;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotaFiscalRepository extends JpaRepository<NotaFiscalEntity, Long> {

    boolean existsByNumero(String numero);

    Optional<NotaFiscalEntity> findByOrdemServico(Long ordemServico);

    @Query("SELECT COALESCE(MAX(n.ordemServico), 0) FROM NotaFiscalEntity n")
    Long findMaxOrdemServico();

    long countByEntregue(boolean entregue);

    long countByEntregueAndDataEmissaoAfter(boolean entregue, LocalDateTime data);

    List<NotaFiscalEntity> findByCargaIsNullAndEntregue(boolean entregue);
}
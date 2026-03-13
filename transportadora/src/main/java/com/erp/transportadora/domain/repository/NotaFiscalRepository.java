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

public interface NotaFiscalRepository extends JpaRepository<NotaFiscalEntity, Long>, JpaSpecificationExecutor<NotaFiscalEntity> {

    Optional<NotaFiscalEntity> findByOrdemServico(Long ordemServico);

    @Query("SELECT COALESCE(MAX(n.ordemServico), 0) FROM NotaFiscalEntity n")
    Long findMaxOrdemServico();

    long countByStatus(StatusNota status);

    long countByStatusAndDataEmissaoAfter(StatusNota status, LocalDateTime data);

    List<NotaFiscalEntity> findByCargaIsNullAndStatus(StatusNota status);

    @Query("SELECT n.cliente.id, n.cliente.nome, n.cidade, COUNT(n), COALESCE(SUM(n.frete), 0) " +
            "FROM NotaFiscalEntity n " +
            "WHERE n.cliente IS NOT NULL " +
            "AND n.status NOT IN ('PENDENTE', 'EM_ROTA') " +
            "GROUP BY n.cliente.id, n.cliente.nome, n.cidade " +
            "ORDER BY n.cliente.nome, COUNT(n) DESC")
    List<Object[]> buscarFaturamentoPorClienteECidade();
}
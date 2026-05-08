package com.erp.transportadora.domain.repository;

import com.erp.transportadora.domain.entity.NotaFiscalEntity;
import com.erp.transportadora.domain.enums.StatusNota;
import org.aspectj.weaver.ast.Not;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotaFiscalRepository extends JpaRepository<NotaFiscalEntity, Long>, JpaSpecificationExecutor<NotaFiscalEntity> {

    long countByStatus(StatusNota status);

    long countByStatusAndDataEmissaoAfter(StatusNota status, LocalDateTime data);

    List<NotaFiscalEntity> findByCargaIsNullAndStatus(StatusNota status);

    List<NotaFiscalEntity> findByClienteId(Long clienteId);

    Optional<NotaFiscalEntity> findByOrdemServico(Long ordemServico);

    @Query("SELECT COALESCE(MAX(n.ordemServico), 0) FROM NotaFiscalEntity n")
    Long findMaxOrdemServico();

    @Query(value = "SELECT n.cliente_id, c.nome, n.cidade, COUNT(n.id), COALESCE(SUM(n.frete), 0) " +
            "FROM notas_fiscais n " +
            "JOIN clientes c ON c.id = n.cliente_id " +
            "WHERE n.cliente_id IS NOT NULL " +
            "AND n.status NOT IN ('PENDENTE', 'EM_ROTA') " +
            "AND n.data_emissao >= :dataInicio " +
            "AND n.data_emissao <= :dataFim " +
            "GROUP BY n.cliente_id, c.nome, n.cidade " +
            "ORDER BY c.nome, COUNT(n.id) DESC", nativeQuery = true)
    List<Object[]> buscarFaturamentoPorClienteECidade(
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );

    @Query(value = "SELECT n.data_emissao, n.numero, n.valor, n.volumes, n.destinatario, n.cidade, n.frete " +
            "FROM notas_fiscais n " +
            "WHERE n.cliente_id = :clienteId " +
            "AND n.status NOT IN ('PENDENTE', 'EM_ROTA') " +
            "AND n.data_emissao >= :dataInicio " +
            "AND n.data_emissao <= :dataFim " +
            "ORDER BY n.data_emissao", nativeQuery = true)
    List<Object[]> buscarNotasPorClienteEPeriodo(
            @Param("clienteId") Long clienteId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
}
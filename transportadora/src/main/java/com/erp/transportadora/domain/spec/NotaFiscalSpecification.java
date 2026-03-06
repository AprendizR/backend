package com.erp.transportadora.domain.spec;

import com.erp.transportadora.domain.entity.NotaFiscalEntity;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class NotaFiscalSpecification {
    public static Specification<NotaFiscalEntity> filtrar(String numero, Long ordemServico, String remetente, String destinatario, LocalDate dataInicio, LocalDate dataFim) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (numero != null)
                predicates.add(criteriaBuilder.like(root.get("numero"), "%" + numero + "%"));
            if (ordemServico != null)
                predicates.add(criteriaBuilder.equal(root.get("ordemServico"), ordemServico));
            if (remetente != null)
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("remetente")), "%" + remetente.toLowerCase() + "%"));
            if (destinatario != null)
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("destinatario")), "%" + destinatario.toLowerCase() + "%"));
            if (dataInicio != null)
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dataEmissao"), dataInicio.atStartOfDay()));

            if (dataFim != null)
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dataEmissao"), dataFim.atTime(23, 59, 59)));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

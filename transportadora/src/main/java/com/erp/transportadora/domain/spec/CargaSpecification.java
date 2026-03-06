package com.erp.transportadora.domain.spec;

import com.erp.transportadora.domain.entity.CargaEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CargaSpecification {

    public static Specification<CargaEntity> filtrar(Long motoristaId, Long veiculoId, Long numeroCarga, LocalDate dataInicio, LocalDate dataFim) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (motoristaId != null)
                predicates.add(criteriaBuilder.equal(root.get("motorista").get("id"), motoristaId));

            if (veiculoId != null)
                predicates.add(criteriaBuilder.equal(root.get("veiculo").get("id"), veiculoId));

            if (numeroCarga != null)
                predicates.add(criteriaBuilder.equal(root.get("numeroRota"), numeroCarga));

            if (dataInicio != null)
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dataCriacao"), dataInicio.atStartOfDay()));

            if (dataFim != null)
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dataCriacao"), dataFim.atTime(23, 59, 59)));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
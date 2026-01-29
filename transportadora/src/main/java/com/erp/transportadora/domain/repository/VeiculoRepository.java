package com.erp.transportadora.domain.repository;

import com.erp.transportadora.domain.entity.MotoristaEntity;
import com.erp.transportadora.domain.entity.VeiculoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VeiculoRepository extends JpaRepository<VeiculoEntity, Long> {
    List<VeiculoEntity> findByPlacaContainingIgnoreCase(String placa);

    boolean existsByPlaca(String placa);

}

package com.erp.transportadora.domain.mapper;

import com.erp.transportadora.domain.entity.CargaEntity;
import com.erp.transportadora.dto.response.*;

public class CargaMapper {

    public static CargaDTODetalhada toDetalhada(CargaEntity carga) {
        return new CargaDTODetalhada(
                carga.getId(),
                carga.getNumeroRota(),
                carga.getStatus(),
                new MotoristaDTOResumo(
                        carga.getMotorista().getId(),
                        carga.getMotorista().getNome()
                ),
                new VeiculoDTOResumo(
                        carga.getVeiculo().getId(),
                        carga.getVeiculo().getPlaca()
                ),
                carga.getNotasFiscais().stream().map(n -> new NotaFiscalDTOResumo(
                                n.getId(),
                                n.getOrdemServico(),
                                n.getNumero(),
                                n.getEntregue())).toList(),
                carga.getDataCriacao()
        );
    }

    public static CargaDTOResumo toResumo(CargaEntity carga) {
        return new CargaDTOResumo(
                carga.getId(),
                carga.getNumeroRota(),
                carga.getStatus(),
                new MotoristaDTOResumo(
                        carga.getMotorista().getId(),
                        carga.getMotorista().getNome()
                ),
                new VeiculoDTOResumo(
                        carga.getVeiculo().getId(),
                        carga.getVeiculo().getPlaca()
                ),
                carga.getDataCriacao()
        );
    }
}
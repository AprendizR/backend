package com.erp.transportadora.domain.mapper;

import com.erp.transportadora.domain.entity.CargaEntity;
import com.erp.transportadora.domain.entity.NotaFiscalEntity;
import com.erp.transportadora.domain.enums.StatusNota;
import com.erp.transportadora.dto.request.MotoristaDTORequest;
import com.erp.transportadora.dto.request.VeiculoDTORequest;
import com.erp.transportadora.dto.response.*;

public class CargaMapper {

    public static CargaDTODetalhada toDetalhada(CargaEntity carga) {
        return new CargaDTODetalhada(
                carga.getId(),
                carga.getNumeroRota(),
                carga.getStatusCarga(),
                new MotoristaDTORequest(
                        carga.getMotorista().getNome(),
                        carga.getMotorista().getApelido(),
                        carga.getMotorista().getCpf(),
                        carga.getMotorista().getTelefone()
                ),
                new VeiculoDTORequest(
                        carga.getVeiculo().getPlaca(),
                        carga.getVeiculo().getModelo()
                ),
                carga.getNotasFiscais().stream()
                        .map(n -> new NotaFiscalDTOResumo(
                                n.getId(),
                                n.getOrdemServico(),
                                n.getNumero(),
                                isFinalizada(n)
                        ))
                        .toList(),
                carga.getDataCriacao()
        );
    }

    public static CargaDTOResumo toResumo(CargaEntity carga) {
        return new CargaDTOResumo(
                carga.getId(),
                carga.getNumeroRota(),
                carga.getStatusCarga(),
                new MotoristaDTORequest(
                        carga.getMotorista().getNome(),
                        carga.getMotorista().getApelido(),
                        carga.getMotorista().getCpf(),
                        carga.getMotorista().getTelefone()
                ),
                new VeiculoDTORequest(
                        carga.getVeiculo().getPlaca(),
                        carga.getVeiculo().getModelo()
                ),
                carga.getDataCriacao()
        );
    }

    private static boolean isFinalizada(NotaFiscalEntity nota) {
        return nota.getStatus() != StatusNota.PENDENTE && nota.getStatus() != StatusNota.EM_ROTA;
    }
}
package com.erp.transportadora.domain.mapper;

import com.erp.transportadora.domain.entity.CargaEntity;
import com.erp.transportadora.domain.entity.NotaFiscalEntity;
import com.erp.transportadora.domain.enums.StatusNota;
import com.erp.transportadora.dto.request.MotoristaDTORequest;
import com.erp.transportadora.dto.request.VeiculoDTORequest;
import com.erp.transportadora.dto.response.*;

public class CargaMapper {

    public static CargaDTODetalhada toDetalhada(CargaEntity carga) {
        MotoristaDTORequest ajudante = carga.getAjudante() != null ? new MotoristaDTORequest(
                carga.getAjudante().getNome(),
                carga.getAjudante().getApelido(),
                carga.getAjudante().getCpf(),
                carga.getAjudante().getTelefone(),
                carga.getAjudante().getValorDiaria()
        ) : null;

        return new CargaDTODetalhada(
                carga.getId(),
                carga.getNumeroRota(),
                carga.getStatusCarga(),
                new MotoristaDTORequest(
                        carga.getMotorista().getNome(),
                        carga.getMotorista().getApelido(),
                        carga.getMotorista().getCpf(),
                        carga.getMotorista().getTelefone(),
                        carga.getMotorista().getValorDiaria()
                ),
                ajudante,
                new VeiculoDTORequest(
                        carga.getVeiculo().getPlaca(),
                        carga.getVeiculo().getModelo()
                ),
                carga.getNotasFiscais().stream()
                        .map(n -> new NotaFiscalDTOResumo(
                                n.getId(),
                                n.getOrdemServico(),
                                n.getNumero(),
                                n.getDestinatario(),
                                n.getCidade(),
                                n.getRemetente(),
                                n.getFotoComprovantePath() != null,
                                n.getFotoComprovantePath() != null && n.getFotoComprovantePath().endsWith(".pdf"),
                                isFinalizada(n)
                        ))
                        .toList(),
                carga.getDataCriacao()
        );
    }

    public static CargaDTOResumo toResumo(CargaEntity carga) {
        MotoristaDTOResponse motorista = new MotoristaDTOResponse(
                carga.getMotorista().getId(),
                carga.getMotorista().getNome(),
                carga.getMotorista().getApelido(),
                carga.getMotorista().getCpf(),
                carga.getMotorista().getTelefone()
        );

        MotoristaDTOResponse ajudante = carga.getAjudante() != null ? new MotoristaDTOResponse(
                carga.getAjudante().getId(),
                carga.getAjudante().getNome(),
                carga.getAjudante().getApelido(),
                carga.getAjudante().getCpf(),
                carga.getAjudante().getTelefone()
        ) : null;

        VeiculoDTOResponse veiculo = new VeiculoDTOResponse(
                carga.getVeiculo().getId(),
                carga.getVeiculo().getPlaca(),
                carga.getVeiculo().getModelo()
        );

        return new CargaDTOResumo(
                carga.getId(),
                carga.getNumeroRota(),
                carga.getStatusCarga(),
                motorista,
                ajudante,
                veiculo,
                carga.getDataCriacao()
        );
    }

    private static boolean isFinalizada(NotaFiscalEntity nota) {
        return nota.getStatus() != StatusNota.PENDENTE && nota.getStatus() != StatusNota.EM_ROTA;
    }
}
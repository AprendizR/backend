package com.erp.transportadora.domain.entity;
import com.erp.transportadora.domain.enums.SubtipoOcorrencia;
import com.erp.transportadora.domain.enums.TipoOcorrencia;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ocorrencias")
public class OcorrenciaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "nota_fiscal_id", nullable = false)
    private NotaFiscalEntity notaFiscal;

    @Enumerated(EnumType.STRING)
    private TipoOcorrencia tipo;

    @Enumerated(EnumType.STRING)
    private SubtipoOcorrencia subtipo;

    @Column(nullable = false)
    private LocalDateTime dataOcorrencia;

    private String nomeRecebedor;
    private String observacao;
    private String urlFotoComprovante;

    @PrePersist
    public void prePersist() {
        if (this.dataOcorrencia == null) {
            this.dataOcorrencia = LocalDateTime.now();
        }
    }
}
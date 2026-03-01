package com.erp.transportadora.domain.entity;

import com.erp.transportadora.domain.enums.StatusNota;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notas_fiscais")
public class NotaFiscalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String numero;

    @Column(name = "ordem_servico", nullable = false, unique = true)
    private Long ordemServico;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private ClienteEntity cliente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusNota status = StatusNota.PENDENTE;

    @Column(name = "foto_comprovante_path")
    private String fotoComprovantePath;

    @ManyToOne
    @JoinColumn(name = "carga_id")
    @JsonIgnoreProperties("notasFiscais")
    private CargaEntity carga;

    @PrePersist
    public void prePersist() {
        this.dataEmissao = LocalDateTime.now();
    }


    private LocalDateTime dataEmissao;
    private String remetente;
    private String destinatario;
    private String cep;
    private String cidade;
    private String endereco;
    private Double valor;
    private Integer volumes;
    private Double latitude;
    private Double longitude;
    private String observacao;
    private Integer ordemEntrega;
    private LocalDateTime dataEntrega;
}
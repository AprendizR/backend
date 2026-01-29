package com.erp.transportadora.domain.entity;
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

    @Column(nullable = false, unique = true)
    private String numero;

    @Column(name = "ordem_servico", nullable = false, unique = true)
    private Long ordemServico;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private ClienteEntity cliente;

    @Column(nullable = false)
    @Builder.Default
    private Boolean entregue = false;

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

}
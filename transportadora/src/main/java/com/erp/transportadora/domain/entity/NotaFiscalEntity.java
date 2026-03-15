package com.erp.transportadora.domain.entity;

import com.erp.transportadora.domain.enums.StatusNota;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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

    @ElementCollection
    @CollectionTable(name = "fotos_nota", joinColumns = @JoinColumn(name = "nota_fiscal_id"))
    @Column(name = "caminho")
    @Builder.Default
    private Set<String> fotos = new LinkedHashSet<>();

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
    private Double frete;
    private Double valor;
    private Integer volumes;
    private String observacao;
    private Integer ordemEntrega;
    private LocalDateTime dataEntrega;
}
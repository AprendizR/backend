package com.erp.transportadora.domain.entity;

import com.erp.transportadora.domain.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cargas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CargaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "veiculo_id")
    private VeiculoEntity veiculo;

    @ManyToOne
    @JoinColumn(name = "motorista_id")
    private MotoristaEntity motorista;

    @OneToMany(mappedBy = "carga", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnoreProperties("carga")
    private List<NotaFiscalEntity> notasFiscais = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(unique = true)
    private Long numeroRota;


    private LocalDateTime dataCriacao;
    private LocalDateTime dataCarregamento;
    private LocalDateTime dataEntrega;
}

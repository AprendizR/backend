package com.erp.transportadora.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "motoristas")
public class MotoristaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String apelido;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false)
    private String telefone;

    @Column(nullable = false)
    @Builder.Default
    private Integer diasComoMotorista = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer diasComoAjudante = 0;

    @Column(nullable = false)
    @Builder.Default
    private Double valorDiaria = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Double descontos = 0.0;

    private Integer ajusteDiasMotorista = 0;
    private Integer ajusteDiasAjudante = 0;
}

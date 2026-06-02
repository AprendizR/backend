package com.erp.transportadora.domain.entity;

import com.erp.transportadora.domain.enums.TipoFrete;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "frete_cliente")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FreteClienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteEntity cliente;

    @Column(nullable = false)
    private String cidade;

    @Column(nullable = false)
    private Double valor;

    @Enumerated(EnumType.STRING)
    @Column
    @Builder.Default
    private TipoFrete tipo = TipoFrete.CIDADE;

    private Double percentual;
    private Double adicional;
}

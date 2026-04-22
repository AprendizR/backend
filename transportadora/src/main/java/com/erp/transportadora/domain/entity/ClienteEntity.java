package com.erp.transportadora.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "clientes")
public class ClienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column (name = "senha_portal")
    private String senhaPortal;

    private String cnpj;
    private String cidade;
    private String endereco;
    private String bairro;
    private String cep;
}

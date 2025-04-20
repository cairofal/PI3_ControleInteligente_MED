package com.suscompanion.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing an item in a medical prescription.
 */
@Entity
@Table(name = "receita_itens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceitaItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receita_id", nullable = false)
    private Receita receita;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicamento_id")
    private Medicamento medicamento;

    @NotBlank(message = "Descrição é obrigatória")
    @Column(name = "descricao", nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @NotBlank(message = "Posologia é obrigatória")
    @Column(name = "posologia", nullable = false, columnDefinition = "TEXT")
    private String posologia;

    @PositiveOrZero(message = "Quantidade deve ser um número positivo ou zero")
    @Column(name = "quantidade")
    private Integer quantidade;

    @CreationTimestamp
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;
}

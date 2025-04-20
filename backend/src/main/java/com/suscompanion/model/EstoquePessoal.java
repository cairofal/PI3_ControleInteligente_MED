package com.suscompanion.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a user's personal medication inventory.
 */
@Entity
@Table(name = "estoque_pessoal")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstoquePessoal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicamento_id", nullable = false)
    private Medicamento medicamento;

    @NotNull(message = "Quantidade atual é obrigatória")
    @Min(value = 0, message = "Quantidade atual não pode ser negativa")
    @Column(name = "quantidade_atual", nullable = false)
    private Integer quantidadeAtual;

    @NotNull(message = "Quantidade de alerta é obrigatória")
    @Positive(message = "Quantidade de alerta deve ser um número positivo")
    @Column(name = "quantidade_alerta")
    private Integer quantidadeAlerta = 5; // Default alert threshold

    @CreationTimestamp
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    /**
     * Check if the stock is low (current quantity is less than or equal to alert quantity).
     * @return true if the stock is low
     */
    @Transient
    public boolean isEstoqueBaixo() {
        return quantidadeAtual <= quantidadeAlerta;
    }
}

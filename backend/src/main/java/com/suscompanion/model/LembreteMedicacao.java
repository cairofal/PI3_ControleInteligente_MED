package com.suscompanion.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a medication reminder.
 */
@Entity
@Table(name = "lembretes_medicacao")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LembreteMedicacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicamento_id", nullable = false)
    private Medicamento medicamento;

    @NotEmpty(message = "Horários são obrigatórios")
    @Column(name = "horarios", nullable = false, columnDefinition = "time[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<LocalTime> horarios;

    @Column(name = "dias_semana", columnDefinition = "integer[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<Integer> diasSemana; // 0-6 (Domingo-Sábado)

    @Positive(message = "Quantidade da dose deve ser um número positivo")
    @Column(name = "quantidade_dose", precision = 10, scale = 2)
    private BigDecimal quantidadeDose;

    @Size(max = 1000, message = "Instruções devem ter no máximo 1000 caracteres")
    @Column(name = "instrucoes", columnDefinition = "TEXT")
    private String instrucoes;

    @NotNull(message = "Status ativo/inativo é obrigatório")
    @Column(name = "ativo")
    private Boolean ativo = true;

    @CreationTimestamp
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    /**
     * Check if the reminder is for today.
     * @return true if the reminder is for today
     */
    @Transient
    public boolean isParaHoje() {
        if (diasSemana == null || diasSemana.isEmpty()) {
            return true; // If no days are specified, it's for every day
        }

        int today = LocalDateTime.now().getDayOfWeek().getValue() % 7; // 0-6 (Sunday-Saturday)

        return diasSemana.contains(today);
    }
}

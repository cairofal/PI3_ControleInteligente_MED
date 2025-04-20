package com.suscompanion.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a health monitoring record.
 */
@Entity
@Table(name = "monitoramento_saude")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonitoramentoSaude {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotNull(message = "Tipo de monitoramento é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoMonitoramento tipo;

    // Blood pressure fields
    @Min(value = 50, message = "Valor sistólica deve ser pelo menos 50")
    @Max(value = 300, message = "Valor sistólica deve ser no máximo 300")
    @Column(name = "valor_sistolica")
    private Integer valorSistolica;

    @Min(value = 30, message = "Valor diastólica deve ser pelo menos 30")
    @Max(value = 200, message = "Valor diastólica deve ser no máximo 200")
    @Column(name = "valor_diastolica")
    private Integer valorDiastolica;

    @Positive(message = "Pulsação deve ser um número positivo")
    @Max(value = 250, message = "Pulsação deve ser no máximo 250")
    @Column(name = "pulsacao")
    private Integer pulsacao;

    // Blood glucose fields
    @Positive(message = "Valor de glicemia deve ser um número positivo")
    @Column(name = "valor_glicemia", precision = 5, scale = 2)
    private BigDecimal valorGlicemia;

    @Column(name = "jejum")
    private Boolean jejum;

    @Size(max = 1000, message = "Observações devem ter no máximo 1000 caracteres")
    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @NotNull(message = "Data de registro é obrigatória")
    @PastOrPresent(message = "Data de registro deve ser no passado ou presente")
    @Column(name = "data_registro", nullable = false)
    private LocalDateTime dataRegistro = LocalDateTime.now();

    @CreationTimestamp
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    /**
     * Enum representing the type of health monitoring.
     */
    public enum TipoMonitoramento {
        PRESSAO,
        GLICEMIA
    }
}

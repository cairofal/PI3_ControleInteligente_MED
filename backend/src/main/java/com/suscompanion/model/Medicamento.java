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
 * Entity representing a medication in the system.
 */
@Entity
@Table(name = "medicamentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Nome completo é obrigatório")
    @Size(max = 100, message = "Nome completo deve ter no máximo 100 caracteres")
    @Column(name = "nome_completo", nullable = false, length = 100)
    private String nomeCompleto;

    @Size(max = 50, message = "Nome simplificado deve ter no máximo 50 caracteres")
    @Column(name = "nome_simplificado", length = 50)
    private String nomeSimplificado;

    @Size(max = 50, message = "Dosagem deve ter no máximo 50 caracteres")
    @Column(length = 50)
    private String dosagem;

    @Size(max = 30, message = "Tipo deve ter no máximo 30 caracteres")
    @Column(length = 30)
    private String tipo;

    @Pattern(regexp = "^(https?|ftp)://.*$", message = "URL da foto deve ser válida")
    @Column(name = "foto_url")
    private String fotoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @CreationTimestamp
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;
}

package com.suscompanion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a refresh token for JWT authentication.
 */
@Entity
@Table(name = "refresh_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private String token;

    @Column(name = "expiracao", nullable = false)
    private LocalDateTime expiracao;

    private Boolean revogado = false;

    @CreationTimestamp
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    /**
     * Check if the token is expired.
     * @return true if the token is expired
     */
    @Transient
    public boolean isExpirado() {
        return LocalDateTime.now().isAfter(expiracao);
    }

    /**
     * Check if the token is valid (not expired and not revoked).
     * @return true if the token is valid
     */
    @Transient
    public boolean isValido() {
        return !isExpirado() && !revogado;
    }
}
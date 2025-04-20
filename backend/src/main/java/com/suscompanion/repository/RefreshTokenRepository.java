package com.suscompanion.repository;

import com.suscompanion.model.RefreshToken;
import com.suscompanion.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for accessing RefreshToken entities.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    /**
     * Find a refresh token by token value.
     * @param token the token value
     * @return an Optional containing the refresh token if found
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Find all refresh tokens for a user.
     * @param usuario the user
     * @return a list of refresh tokens for the user
     */
    List<RefreshToken> findByUsuario(Usuario usuario);

    /**
     * Find all refresh tokens for a user by ID.
     * @param usuarioId the user ID
     * @return a list of refresh tokens for the user
     */
    List<RefreshToken> findByUsuarioId(UUID usuarioId);

    /**
     * Delete all expired tokens.
     * @param now the current date and time
     * @return the number of tokens deleted
     */
    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.expiracao < :now")
    int deleteAllExpired(@Param("now") LocalDateTime now);

    /**
     * Revoke all tokens for a user.
     * @param usuarioId the user ID
     * @return the number of tokens revoked
     */
    @Modifying
    @Query("UPDATE RefreshToken r SET r.revogado = true WHERE r.usuario.id = :usuarioId")
    int revokeAllByUsuarioId(@Param("usuarioId") UUID usuarioId);

    /**
     * Delete all tokens for a user.
     * @param usuarioId the user ID
     */
    void deleteByUsuarioId(UUID usuarioId);
}
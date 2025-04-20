package com.suscompanion.repository;

import com.suscompanion.model.Receita;
import com.suscompanion.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for accessing Receita entities.
 */
@Repository
public interface ReceitaRepository extends JpaRepository<Receita, UUID> {

    /**
     * Find prescriptions by user.
     * @param usuario the user
     * @param pageable pagination information
     * @return a page of prescriptions for the user
     */
    Page<Receita> findByUsuario(Usuario usuario, Pageable pageable);

    /**
     * Find prescriptions by user ID.
     * @param usuarioId the user ID
     * @return a list of prescriptions for the user
     */
    List<Receita> findByUsuarioId(UUID usuarioId);

    /**
     * Find prescription by ID and user.
     * @param id the prescription ID
     * @param usuario the user
     * @return an Optional containing the prescription if found
     */
    Optional<Receita> findByIdAndUsuario(UUID id, Usuario usuario);

    /**
     * Find prescription by ID and user ID.
     * @param id the prescription ID
     * @param usuarioId the user ID
     * @return an Optional containing the prescription if found
     */
    Optional<Receita> findByIdAndUsuarioId(UUID id, UUID usuarioId);

    /**
     * Find active prescriptions (not expired) for a user.
     * @param usuarioId the user ID
     * @param hoje the current date
     * @return a list of active prescriptions
     */
    @Query("SELECT r FROM Receita r WHERE r.usuario.id = :usuarioId AND " +
           "(r.dataValidade IS NULL OR r.dataValidade >= :hoje)")
    List<Receita> findReceitasAtivas(@Param("usuarioId") UUID usuarioId, 
                                    @Param("hoje") LocalDate hoje);

    /**
     * Find prescriptions by doctor name for a user.
     * @param medicoNome the doctor name to search for
     * @param usuarioId the user ID
     * @param pageable pagination information
     * @return a page of prescriptions matching the search criteria
     */
    @Query("SELECT r FROM Receita r WHERE r.usuario.id = :usuarioId AND " +
           "LOWER(r.medicoNome) LIKE LOWER(CONCAT('%', :medicoNome, '%'))")
    Page<Receita> searchByMedicoNome(@Param("medicoNome") String medicoNome, 
                                    @Param("usuarioId") UUID usuarioId,
                                    Pageable pageable);
}
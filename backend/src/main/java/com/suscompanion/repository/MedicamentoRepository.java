package com.suscompanion.repository;

import com.suscompanion.model.Medicamento;
import com.suscompanion.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for accessing Medicamento entities.
 */
@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, UUID> {

    /**
     * Find medications by user.
     * @param usuario the user
     * @param pageable pagination information
     * @return a page of medications for the user
     */
    Page<Medicamento> findByUsuario(Usuario usuario, Pageable pageable);

    /**
     * Find medications by user ID.
     * @param usuarioId the user ID
     * @return a list of medications for the user
     */
    List<Medicamento> findByUsuarioId(UUID usuarioId);

    /**
     * Find medication by ID and user.
     * @param id the medication ID
     * @param usuario the user
     * @return an Optional containing the medication if found
     */
    Optional<Medicamento> findByIdAndUsuario(UUID id, Usuario usuario);

    /**
     * Find medication by ID and user ID.
     * @param id the medication ID
     * @param usuarioId the user ID
     * @return an Optional containing the medication if found
     */
    Optional<Medicamento> findByIdAndUsuarioId(UUID id, UUID usuarioId);

    /**
     * Search medications by name for a user.
     * @param nome the name to search for
     * @param usuarioId the user ID
     * @param pageable pagination information
     * @return a page of medications matching the search criteria
     */
    @Query("SELECT m FROM Medicamento m WHERE m.usuario.id = :usuarioId AND " +
           "(LOWER(m.nomeCompleto) LIKE LOWER(CONCAT('%', :nome, '%')) OR " +
           "LOWER(m.nomeSimplificado) LIKE LOWER(CONCAT('%', :nome, '%')))")
    Page<Medicamento> searchByNome(@Param("nome") String nome, 
                                  @Param("usuarioId") UUID usuarioId,
                                  Pageable pageable);
}
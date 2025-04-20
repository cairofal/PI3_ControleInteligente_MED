package com.suscompanion.repository;

import com.suscompanion.model.LembreteMedicacao;
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
 * Repository for accessing LembreteMedicacao entities.
 */
@Repository
public interface LembreteMedicacaoRepository extends JpaRepository<LembreteMedicacao, UUID> {

    /**
     * Find medication reminders by user.
     * @param usuario the user
     * @param pageable pagination information
     * @return a page of medication reminders for the user
     */
    Page<LembreteMedicacao> findByUsuario(Usuario usuario, Pageable pageable);

    /**
     * Find medication reminders by user ID.
     * @param usuarioId the user ID
     * @return a list of medication reminders for the user
     */
    List<LembreteMedicacao> findByUsuarioId(UUID usuarioId);

    /**
     * Find medication reminders by medication.
     * @param medicamento the medication
     * @return a list of medication reminders for the medication
     */
    List<LembreteMedicacao> findByMedicamento(Medicamento medicamento);

    /**
     * Find medication reminders by medication ID.
     * @param medicamentoId the medication ID
     * @return a list of medication reminders for the medication
     */
    List<LembreteMedicacao> findByMedicamentoId(UUID medicamentoId);

    /**
     * Find medication reminder by ID and user.
     * @param id the medication reminder ID
     * @param usuario the user
     * @return an Optional containing the medication reminder if found
     */
    Optional<LembreteMedicacao> findByIdAndUsuario(UUID id, Usuario usuario);

    /**
     * Find medication reminder by ID and user ID.
     * @param id the medication reminder ID
     * @param usuarioId the user ID
     * @return an Optional containing the medication reminder if found
     */
    Optional<LembreteMedicacao> findByIdAndUsuarioId(UUID id, UUID usuarioId);

    /**
     * Find active medication reminders for today for a user.
     * @param usuarioId the user ID
     * @param diaAtual the day of the week (0-6, Sunday-Saturday)
     * @return a list of active medication reminders for today
     */
    @Query("""
    SELECT l FROM LembreteMedicacao l
    WHERE l.usuario.id = :usuarioId
    AND l.ativo = true
    AND (SIZE(l.diasSemana) = 0 OR :diaAtual IN (SELECT d FROM LembreteMedicacao lm JOIN lm.diasSemana d WHERE lm = l))
    """)
    List<LembreteMedicacao> findLembretesHoje(
            @Param("usuarioId") UUID usuarioId,
            @Param("diaAtual") Integer diaAtual
    );







    /**
     * Find active medication reminders by user ID.
     * @param usuarioId the user ID
     * @return a list of active medication reminders
     */
    List<LembreteMedicacao> findByUsuarioIdAndAtivoTrue(UUID usuarioId);
}
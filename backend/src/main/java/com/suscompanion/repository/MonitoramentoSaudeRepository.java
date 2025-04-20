package com.suscompanion.repository;

import com.suscompanion.model.MonitoramentoSaude;
import com.suscompanion.model.MonitoramentoSaude.TipoMonitoramento;
import com.suscompanion.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for accessing MonitoramentoSaude entities.
 */
@Repository
public interface MonitoramentoSaudeRepository extends JpaRepository<MonitoramentoSaude, UUID> {

    /**
     * Find health monitoring records by user.
     * @param usuario the user
     * @param pageable pagination information
     * @return a page of health monitoring records for the user
     */
    Page<MonitoramentoSaude> findByUsuario(Usuario usuario, Pageable pageable);

    /**
     * Find health monitoring records by user ID.
     * @param usuarioId the user ID
     * @return a list of health monitoring records for the user
     */
    List<MonitoramentoSaude> findByUsuarioId(UUID usuarioId);

    /**
     * Find health monitoring record by ID and user.
     * @param id the health monitoring record ID
     * @param usuario the user
     * @return an Optional containing the health monitoring record if found
     */
    Optional<MonitoramentoSaude> findByIdAndUsuario(UUID id, Usuario usuario);

    /**
     * Find health monitoring record by ID and user ID.
     * @param id the health monitoring record ID
     * @param usuarioId the user ID
     * @return an Optional containing the health monitoring record if found
     */
    Optional<MonitoramentoSaude> findByIdAndUsuarioId(UUID id, UUID usuarioId);

    /**
     * Find health monitoring records by type for a user.
     * @param tipo the type of health monitoring
     * @param usuarioId the user ID
     * @param pageable pagination information
     * @return a page of health monitoring records matching the criteria
     */
    Page<MonitoramentoSaude> findByTipoAndUsuarioId(TipoMonitoramento tipo, UUID usuarioId, Pageable pageable);

    /**
     * Find the latest health monitoring records for a user.
     * @param usuarioId the user ID
     * @param limit the maximum number of records to return
     * @return a list of the latest health monitoring records
     */
    @Query("SELECT m FROM MonitoramentoSaude m WHERE m.usuario.id = :usuarioId " +
           "ORDER BY m.dataRegistro DESC")
    List<MonitoramentoSaude> findUltimosRegistros(@Param("usuarioId") UUID usuarioId, Pageable pageable);

    /**
     * Find health monitoring records within a date range for a user.
     * @param usuarioId the user ID
     * @param inicio the start date
     * @param fim the end date
     * @param pageable pagination information
     * @return a page of health monitoring records within the date range
     */
    Page<MonitoramentoSaude> findByUsuarioIdAndDataRegistroBetween(
            UUID usuarioId, LocalDateTime inicio, LocalDateTime fim, Pageable pageable);
}
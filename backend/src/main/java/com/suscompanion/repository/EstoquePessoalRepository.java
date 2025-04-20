package com.suscompanion.repository;

import com.suscompanion.model.EstoquePessoal;
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
 * Repository for accessing EstoquePessoal entities.
 */
@Repository
public interface EstoquePessoalRepository extends JpaRepository<EstoquePessoal, UUID> {

    /**
     * Find inventory items by user.
     * @param usuario the user
     * @param pageable pagination information
     * @return a page of inventory items for the user
     */
    Page<EstoquePessoal> findByUsuario(Usuario usuario, Pageable pageable);

    /**
     * Find inventory items by user ID.
     * @param usuarioId the user ID
     * @return a list of inventory items for the user
     */
    List<EstoquePessoal> findByUsuarioId(UUID usuarioId);

    /**
     * Find inventory items by medication.
     * @param medicamento the medication
     * @param pageable pagination information
     * @return a page of inventory items for the medication
     */
    Page<EstoquePessoal> findByMedicamento(Medicamento medicamento, Pageable pageable);

    /**
     * Find inventory item by user and medication.
     * @param usuario the user
     * @param medicamento the medication
     * @return an Optional containing the inventory item if found
     */
    Optional<EstoquePessoal> findByUsuarioAndMedicamento(Usuario usuario, Medicamento medicamento);

    /**
     * Find inventory item by user ID and medication ID.
     * @param usuarioId the user ID
     * @param medicamentoId the medication ID
     * @return an Optional containing the inventory item if found
     */
    Optional<EstoquePessoal> findByUsuarioIdAndMedicamentoId(UUID usuarioId, UUID medicamentoId);

    /**
     * Find inventory items with low stock for a user.
     * @param usuarioId the user ID
     * @return a list of inventory items with low stock
     */
    @Query("SELECT e FROM EstoquePessoal e WHERE e.usuario.id = :usuarioId AND " +
           "e.quantidadeAtual <= e.quantidadeAlerta")
    List<EstoquePessoal> findEstoqueBaixo(@Param("usuarioId") UUID usuarioId);
}
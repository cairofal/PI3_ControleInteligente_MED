package com.suscompanion.controller;

import com.suscompanion.dto.medicamento.MedicamentoDTO;
import com.suscompanion.dto.medicamento.MedicamentoRequest;
import com.suscompanion.dto.usuario.UsuarioDTO;
import com.suscompanion.service.MedicamentoService;
import com.suscompanion.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

/**
 * Controller for medication operations.
 */
@RestController
@RequestMapping("/medicamentos")
@RequiredArgsConstructor
@Tag(name = "Medicamentos", description = "Endpoints para gerenciamento de medicamentos")
@SecurityRequirement(name = "JWT")
public class MedicamentoController {

    private final MedicamentoService medicamentoService;
    private final UsuarioService usuarioService;

    /**
     * Get all medications for the current user.
     * @param pageable pagination information
     * @return a page of medication DTOs
     */
    @GetMapping
    @Operation(summary = "Listar medicamentos", description = "Retorna todos os medicamentos do usuário autenticado")
    public ResponseEntity<Page<MedicamentoDTO>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        UUID usuarioId = getCurrentUserId();
        return ResponseEntity.ok(medicamentoService.getAllByUsuario(usuarioId, pageable));
    }

    /**
     * Get a medication by ID for the current user.
     * @param id the medication ID
     * @return the medication DTO
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obter medicamento por ID", description = "Retorna um medicamento específico do usuário autenticado")
    public ResponseEntity<MedicamentoDTO> getById(@PathVariable UUID id) {
        UUID usuarioId = getCurrentUserId();
        return ResponseEntity.ok(medicamentoService.getByIdAndUsuario(id, usuarioId));
    }

    /**
     * Create a new medication for the current user.
     * @param request the medication creation request
     * @return the created medication DTO
     */
    @PostMapping
    @Operation(summary = "Cadastrar medicamento", description = "Cadastra um novo medicamento para o usuário autenticado")
    public ResponseEntity<MedicamentoDTO> create(@Valid @RequestBody MedicamentoRequest request) {
        UUID usuarioId = getCurrentUserId();
        MedicamentoDTO medicamento = medicamentoService.create(usuarioId, request);
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(medicamento.getId())
                .toUri();
        
        return ResponseEntity.created(location).body(medicamento);
    }

    /**
     * Update a medication for the current user.
     * @param id the medication ID
     * @param request the medication update request
     * @return the updated medication DTO
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar medicamento", description = "Atualiza um medicamento específico do usuário autenticado")
    public ResponseEntity<MedicamentoDTO> update(@PathVariable UUID id, @Valid @RequestBody MedicamentoRequest request) {
        UUID usuarioId = getCurrentUserId();
        return ResponseEntity.ok(medicamentoService.update(id, usuarioId, request));
    }

    /**
     * Delete a medication for the current user.
     * @param id the medication ID
     * @return a response entity with no content
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir medicamento", description = "Exclui um medicamento específico do usuário autenticado")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID usuarioId = getCurrentUserId();
        medicamentoService.delete(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Search medications by name for the current user.
     * @param nome the name to search for
     * @param pageable pagination information
     * @return a page of medication DTOs
     */
    @GetMapping("/search")
    @Operation(summary = "Buscar medicamentos por nome", description = "Busca medicamentos pelo nome para o usuário autenticado")
    public ResponseEntity<Page<MedicamentoDTO>> searchByNome(
            @RequestParam String nome,
            @PageableDefault(size = 20) Pageable pageable) {
        UUID usuarioId = getCurrentUserId();
        return ResponseEntity.ok(medicamentoService.searchByNome(nome, usuarioId, pageable));
    }

    /**
     * Get the current user ID.
     * @return the current user ID
     */
    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UsuarioDTO usuario = usuarioService.getByEmail(email);
        return usuario.getId();
    }
}
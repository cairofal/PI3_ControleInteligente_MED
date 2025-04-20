package com.suscompanion.controller;

import com.suscompanion.dto.receita.ReceitaDTO;
import com.suscompanion.dto.receita.ReceitaRequest;
import com.suscompanion.dto.usuario.UsuarioDTO;
import com.suscompanion.service.ReceitaService;
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
import java.util.List;
import java.util.UUID;

/**
 * Controller for prescription operations.
 */
@RestController
@RequestMapping("/receitas")
@RequiredArgsConstructor
@Tag(name = "Receitas", description = "Endpoints para gerenciamento de receitas médicas")
@SecurityRequirement(name = "JWT")
public class ReceitaController {

    private final ReceitaService receitaService;
    private final UsuarioService usuarioService;

    /**
     * Get all prescriptions for the current user.
     * @param pageable pagination information
     * @return a page of prescription DTOs
     */
    @GetMapping
    @Operation(summary = "Listar receitas", description = "Retorna todas as receitas do usuário autenticado")
    public ResponseEntity<Page<ReceitaDTO>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        UUID usuarioId = getCurrentUserId();
        return ResponseEntity.ok(receitaService.getAllByUsuario(usuarioId, pageable));
    }

    /**
     * Get a prescription by ID for the current user.
     * @param id the prescription ID
     * @return the prescription DTO
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obter receita por ID", description = "Retorna uma receita específica do usuário autenticado")
    public ResponseEntity<ReceitaDTO> getById(@PathVariable UUID id) {
        UUID usuarioId = getCurrentUserId();
        return ResponseEntity.ok(receitaService.getByIdAndUsuario(id, usuarioId));
    }

    /**
     * Create a new prescription for the current user.
     * @param request the prescription creation request
     * @return the created prescription DTO
     */
    @PostMapping
    @Operation(summary = "Cadastrar receita", description = "Cadastra uma nova receita para o usuário autenticado")
    public ResponseEntity<ReceitaDTO> create(@Valid @RequestBody ReceitaRequest request) {
        UUID usuarioId = getCurrentUserId();
        ReceitaDTO receita = receitaService.create(usuarioId, request);
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(receita.getId())
                .toUri();
        
        return ResponseEntity.created(location).body(receita);
    }

    /**
     * Update a prescription for the current user.
     * @param id the prescription ID
     * @param request the prescription update request
     * @return the updated prescription DTO
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar receita", description = "Atualiza uma receita específica do usuário autenticado")
    public ResponseEntity<ReceitaDTO> update(@PathVariable UUID id, @Valid @RequestBody ReceitaRequest request) {
        UUID usuarioId = getCurrentUserId();
        return ResponseEntity.ok(receitaService.update(id, usuarioId, request));
    }

    /**
     * Delete a prescription for the current user.
     * @param id the prescription ID
     * @return a response entity with no content
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir receita", description = "Exclui uma receita específica do usuário autenticado")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID usuarioId = getCurrentUserId();
        receitaService.delete(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get active prescriptions for the current user.
     * @return a list of active prescription DTOs
     */
    @GetMapping("/ativas")
    @Operation(summary = "Listar receitas ativas", description = "Retorna todas as receitas ativas do usuário autenticado")
    public ResponseEntity<List<ReceitaDTO>> getReceitasAtivas() {
        UUID usuarioId = getCurrentUserId();
        return ResponseEntity.ok(receitaService.getReceitasAtivas(usuarioId));
    }

    /**
     * Search prescriptions by doctor name for the current user.
     * @param medicoNome the doctor name to search for
     * @param pageable pagination information
     * @return a page of prescription DTOs
     */
    @GetMapping("/search")
    @Operation(summary = "Buscar receitas por médico", description = "Busca receitas pelo nome do médico para o usuário autenticado")
    public ResponseEntity<Page<ReceitaDTO>> searchByMedicoNome(
            @RequestParam String medicoNome,
            @PageableDefault(size = 20) Pageable pageable) {
        UUID usuarioId = getCurrentUserId();
        return ResponseEntity.ok(receitaService.searchByMedicoNome(medicoNome, usuarioId, pageable));
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
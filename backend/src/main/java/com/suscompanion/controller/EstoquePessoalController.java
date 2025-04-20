package com.suscompanion.controller;

import com.suscompanion.dto.estoque.EstoquePessoalDTO;
import com.suscompanion.dto.estoque.EstoquePessoalRequest;
import com.suscompanion.dto.usuario.UsuarioDTO;
import com.suscompanion.service.EstoquePessoalService;
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
 * Controller for personal inventory operations.
 */
@RestController
@RequestMapping("/estoque")
@RequiredArgsConstructor
@Tag(name = "Estoque Pessoal", description = "Endpoints para gerenciamento de estoque pessoal de medicamentos")
@SecurityRequirement(name = "JWT")
public class EstoquePessoalController {

    private final EstoquePessoalService estoquePessoalService;
    private final UsuarioService usuarioService;

    /**
     * Get all inventory items for the current user.
     * @param pageable pagination information
     * @return a page of inventory item DTOs
     */
    @GetMapping
    @Operation(summary = "Listar estoque", description = "Retorna todos os itens do estoque pessoal do usuário autenticado")
    public ResponseEntity<Page<EstoquePessoalDTO>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        UUID usuarioId = getCurrentUserId();
        return ResponseEntity.ok(estoquePessoalService.getAllByUsuario(usuarioId, pageable));
    }

    /**
     * Get an inventory item by ID for the current user.
     * @param id the inventory item ID
     * @return the inventory item DTO
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obter item do estoque por ID", description = "Retorna um item específico do estoque pessoal do usuário autenticado")
    public ResponseEntity<EstoquePessoalDTO> getById(@PathVariable UUID id) {
        UUID usuarioId = getCurrentUserId();
        return ResponseEntity.ok(estoquePessoalService.getById(id, usuarioId));
    }

    /**
     * Create a new inventory item for the current user.
     * @param request the inventory item creation request
     * @return the created inventory item DTO
     */
    @PostMapping
    @Operation(summary = "Adicionar item ao estoque", description = "Adiciona um novo item ao estoque pessoal do usuário autenticado")
    public ResponseEntity<EstoquePessoalDTO> create(@Valid @RequestBody EstoquePessoalRequest request) {
        UUID usuarioId = getCurrentUserId();
        EstoquePessoalDTO estoque = estoquePessoalService.create(usuarioId, request);
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(estoque.getId())
                .toUri();
        
        return ResponseEntity.created(location).body(estoque);
    }

    /**
     * Update an inventory item for the current user.
     * @param id the inventory item ID
     * @param request the inventory item update request
     * @return the updated inventory item DTO
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar item do estoque", description = "Atualiza um item específico do estoque pessoal do usuário autenticado")
    public ResponseEntity<EstoquePessoalDTO> update(@PathVariable UUID id, @Valid @RequestBody EstoquePessoalRequest request) {
        UUID usuarioId = getCurrentUserId();
        return ResponseEntity.ok(estoquePessoalService.update(id, usuarioId, request));
    }

    /**
     * Delete an inventory item for the current user.
     * @param id the inventory item ID
     * @return a response entity with no content
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir item do estoque", description = "Exclui um item específico do estoque pessoal do usuário autenticado")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID usuarioId = getCurrentUserId();
        estoquePessoalService.delete(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get inventory items with low stock for the current user.
     * @return a list of inventory item DTOs with low stock
     */
    @GetMapping("/baixo")
    @Operation(summary = "Listar medicamentos com estoque baixo", description = "Retorna todos os medicamentos com estoque baixo do usuário autenticado")
    public ResponseEntity<List<EstoquePessoalDTO>> getEstoqueBaixo() {
        UUID usuarioId = getCurrentUserId();
        return ResponseEntity.ok(estoquePessoalService.getEstoqueBaixo(usuarioId));
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
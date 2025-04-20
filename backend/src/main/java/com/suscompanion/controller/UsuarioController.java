package com.suscompanion.controller;

import com.suscompanion.dto.usuario.UsuarioDTO;
import com.suscompanion.dto.usuario.UsuarioRequest;
import com.suscompanion.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller for user operations.
 */
@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Endpoints para gerenciamento de usuários")
@SecurityRequirement(name = "JWT")
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Get the current user.
     * @return the current user DTO
     */
    @GetMapping("/me")
    @Operation(summary = "Obter usuário atual", description = "Retorna os dados do usuário autenticado")
    public ResponseEntity<UsuarioDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return ResponseEntity.ok(usuarioService.getByEmail(email));
    }

    /**
     * Get a user by ID.
     * @param id the user ID
     * @return the user DTO
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obter usuário por ID", description = "Retorna os dados de um usuário específico")
    public ResponseEntity<UsuarioDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(usuarioService.getById(id));
    }

    /**
     * Update a user.
     * @param id the user ID
     * @param request the user update request
     * @return the updated user DTO
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário específico")
    public ResponseEntity<UsuarioDTO> update(@PathVariable UUID id, @Valid @RequestBody UsuarioRequest request) {
        // Check if the authenticated user is updating their own profile
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UsuarioDTO currentUser = usuarioService.getByEmail(email);
        
        if (!currentUser.getId().equals(id)) {
            return ResponseEntity.status(403).build(); // Forbidden
        }
        
        return ResponseEntity.ok(usuarioService.update(id, request));
    }

    /**
     * Delete a user.
     * @param id the user ID
     * @return a response entity with no content
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir usuário", description = "Exclui um usuário específico")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        // Check if the authenticated user is deleting their own profile
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UsuarioDTO currentUser = usuarioService.getByEmail(email);
        
        if (!currentUser.getId().equals(id)) {
            return ResponseEntity.status(403).build(); // Forbidden
        }
        
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
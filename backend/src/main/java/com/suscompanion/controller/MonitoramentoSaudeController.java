package com.suscompanion.controller;

import com.suscompanion.dto.saude.MonitoramentoSaudeDTO;
import com.suscompanion.dto.saude.MonitoramentoSaudeRequest;
import com.suscompanion.dto.usuario.UsuarioDTO;
import com.suscompanion.model.MonitoramentoSaude.TipoMonitoramento;
import com.suscompanion.service.MonitoramentoSaudeService;
import com.suscompanion.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Controller for health monitoring operations.
 */
@RestController
@RequestMapping("/saude")
@RequiredArgsConstructor
@Tag(name = "Monitoramento de Saúde", description = "Endpoints para gerenciamento de monitoramento de saúde")
@SecurityRequirement(name = "JWT")
public class MonitoramentoSaudeController {

    private final MonitoramentoSaudeService monitoramentoSaudeService;
    private final UsuarioService usuarioService;

    /**
     * Get all health monitoring records for the current user.
     * @param pageable pagination information
     * @return a page of health monitoring record DTOs
     */
    @GetMapping
    @Operation(summary = "Listar registros de saúde", description = "Retorna todos os registros de saúde do usuário autenticado")
    public ResponseEntity<Page<MonitoramentoSaudeDTO>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        UUID usuarioId = getCurrentUserId();
        return ResponseEntity.ok(monitoramentoSaudeService.getAllByUsuario(usuarioId, pageable));
    }

    /**
     * Get a health monitoring record by ID for the current user.
     * @param id the health monitoring record ID
     * @return the health monitoring record DTO
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obter registro de saúde por ID", description = "Retorna um registro de saúde específico do usuário autenticado")
    public ResponseEntity<MonitoramentoSaudeDTO> getById(@PathVariable UUID id) {
        UUID usuarioId = getCurrentUserId();
        return ResponseEntity.ok(monitoramentoSaudeService.getByIdAndUsuario(id, usuarioId));
    }

    /**
     * Create a new health monitoring record for the current user.
     * @param request the health monitoring record creation request
     * @return the created health monitoring record DTO
     */
    @PostMapping
    @Operation(summary = "Adicionar registro de saúde", description = "Adiciona um novo registro de saúde para o usuário autenticado")
    public ResponseEntity<MonitoramentoSaudeDTO> create(@Valid @RequestBody MonitoramentoSaudeRequest request) {
        UUID usuarioId = getCurrentUserId();
        MonitoramentoSaudeDTO monitoramento = monitoramentoSaudeService.create(usuarioId, request);
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(monitoramento.getId())
                .toUri();
        
        return ResponseEntity.created(location).body(monitoramento);
    }

    /**
     * Update a health monitoring record for the current user.
     * @param id the health monitoring record ID
     * @param request the health monitoring record update request
     * @return the updated health monitoring record DTO
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar registro de saúde", description = "Atualiza um registro de saúde específico do usuário autenticado")
    public ResponseEntity<MonitoramentoSaudeDTO> update(@PathVariable UUID id, @Valid @RequestBody MonitoramentoSaudeRequest request) {
        UUID usuarioId = getCurrentUserId();
        return ResponseEntity.ok(monitoramentoSaudeService.update(id, usuarioId, request));
    }

    /**
     * Delete a health monitoring record for the current user.
     * @param id the health monitoring record ID
     * @return a response entity with no content
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir registro de saúde", description = "Exclui um registro de saúde específico do usuário autenticado")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID usuarioId = getCurrentUserId();
        monitoramentoSaudeService.delete(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get health monitoring records by type for the current user.
     * @param tipo the type of health monitoring
     * @param pageable pagination information
     * @return a page of health monitoring record DTOs
     */
    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Listar registros por tipo", description = "Retorna todos os registros de saúde de um tipo específico do usuário autenticado")
    public ResponseEntity<Page<MonitoramentoSaudeDTO>> getByTipo(
            @PathVariable TipoMonitoramento tipo,
            @PageableDefault(size = 20) Pageable pageable) {
        UUID usuarioId = getCurrentUserId();
        return ResponseEntity.ok(monitoramentoSaudeService.getByTipo(tipo, usuarioId, pageable));
    }

    /**
     * Get the latest health monitoring records for the current user.
     * @param limit the maximum number of records to return (default: 10)
     * @return a list of the latest health monitoring record DTOs
     */
    @GetMapping("/ultimos-registros")
    @Operation(summary = "Obter últimos registros", description = "Retorna os últimos registros de saúde do usuário autenticado")
    public ResponseEntity<List<MonitoramentoSaudeDTO>> getUltimosRegistros(@RequestParam(defaultValue = "10") int limit) {
        UUID usuarioId = getCurrentUserId();
        return ResponseEntity.ok(monitoramentoSaudeService.getUltimosRegistros(usuarioId, limit));
    }

    /**
     * Get health monitoring records within a date range for the current user.
     * @param inicio the start date
     * @param fim the end date
     * @param pageable pagination information
     * @return a page of health monitoring record DTOs within the date range
     */
    @GetMapping("/periodo")
    @Operation(summary = "Listar registros por período", description = "Retorna todos os registros de saúde dentro de um período específico do usuário autenticado")
    public ResponseEntity<Page<MonitoramentoSaudeDTO>> getByPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
            @PageableDefault(size = 20) Pageable pageable) {
        UUID usuarioId = getCurrentUserId();
        return ResponseEntity.ok(monitoramentoSaudeService.getByDataRegistroBetween(usuarioId, inicio, fim, pageable));
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
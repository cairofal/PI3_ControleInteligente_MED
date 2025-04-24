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


@RestController
@RequestMapping("/saude")
@RequiredArgsConstructor
@Tag(name = "Monitoramento de Saúde", description = "Endpoints para gerenciamento de monitoramento de saúde")
@SecurityRequirement(name = "JWT")
public class MonitoramentoSaudeController {

    private final MonitoramentoSaudeService monitoramentoSaudeService;
    private final UsuarioService usuarioService;


    @GetMapping
    @Operation(summary = "Listar registros de saúde", description = "Retorna todos os registros de saúde do usuário autenticado")
    public ResponseEntity<Page<MonitoramentoSaudeDTO>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        UUID usuarioId = getCurrentUserId();
        return ResponseEntity.ok(monitoramentoSaudeService.getAllByUsuario(usuarioId, pageable));
    }


    @GetMapping("/{id}")
    @Operation(summary = "Obter registro de saúde por ID", description = "Retorna um registro de saúde específico do usuário autenticado")
    public ResponseEntity<MonitoramentoSaudeDTO> getById(@PathVariable UUID id) {
        UUID usuarioId = getCurrentUserId();
        return ResponseEntity.ok(monitoramentoSaudeService.getByIdAndUsuario(id, usuarioId));
    }


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


    @PutMapping("/{id}")
    @Operation(summary = "Atualizar registro de saúde", description = "Atualiza um registro de saúde específico do usuário autenticado")
    public ResponseEntity<MonitoramentoSaudeDTO> update(@PathVariable UUID id, @Valid @RequestBody MonitoramentoSaudeRequest request) {
        UUID usuarioId = getCurrentUserId();
        return ResponseEntity.ok(monitoramentoSaudeService.update(id, usuarioId, request));
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir registro de saúde", description = "Exclui um registro de saúde específico do usuário autenticado")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID usuarioId = getCurrentUserId();
        monitoramentoSaudeService.delete(id, usuarioId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Listar registros por tipo", description = "Retorna todos os registros de saúde de um tipo específico do usuário autenticado")
    public ResponseEntity<Page<MonitoramentoSaudeDTO>> getByTipo(
            @PathVariable TipoMonitoramento tipo,
            @PageableDefault(size = 20) Pageable pageable) {
        UUID usuarioId = getCurrentUserId();
        return ResponseEntity.ok(monitoramentoSaudeService.getByTipo(tipo, usuarioId, pageable));
    }


    @GetMapping("/ultimos-registros")
    @Operation(summary = "Obter últimos registros", description = "Retorna os últimos registros de saúde do usuário autenticado")
    public ResponseEntity<List<MonitoramentoSaudeDTO>> getUltimosRegistros(@RequestParam(defaultValue = "10") int limit) {
        UUID usuarioId = getCurrentUserId();
        return ResponseEntity.ok(monitoramentoSaudeService.getUltimosRegistros(usuarioId, limit));
    }


    @GetMapping("/periodo")
    @Operation(summary = "Listar registros por período", description = "Retorna todos os registros de saúde dentro de um período específico do usuário autenticado")
    public ResponseEntity<Page<MonitoramentoSaudeDTO>> getByPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
            @PageableDefault(size = 20) Pageable pageable) {
        UUID usuarioId = getCurrentUserId();
        return ResponseEntity.ok(monitoramentoSaudeService.getByDataRegistroBetween(usuarioId, inicio, fim, pageable));
    }


    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UsuarioDTO usuario = usuarioService.getByEmail(email);
        return usuario.getId();
    }
}
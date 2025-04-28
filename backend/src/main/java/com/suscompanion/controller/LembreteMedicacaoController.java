package com.suscompanion.controller;

import com.suscompanion.dto.lembrete.LembreteMedicacaoDTO;
import com.suscompanion.dto.lembrete.LembreteMedicacaoRequest;
import com.suscompanion.dto.usuario.UsuarioDTO;
import com.suscompanion.service.LembreteMedicacaoService;
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


@RestController
@RequestMapping("/lembretes")
@RequiredArgsConstructor
@Tag(name = "Lembretes de Medicação", description = "Endpoints para gerenciamento de lembretes de medicação")
@SecurityRequirement(name = "JWT")
public class LembreteMedicacaoController {

    private final LembreteMedicacaoService lembreteMedicacaoService;
    private final UsuarioService usuarioService;


    @GetMapping
    @Operation(summary = "Listar lembretes", description = "Retorna todos os lembretes de medicação do usuário autenticado")
    public ResponseEntity<Page<LembreteMedicacaoDTO>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        UUID usuarioId = getCurrentUserId();
        return ResponseEntity.ok(lembreteMedicacaoService.getAllByUsuario(usuarioId, pageable));
    }


    @GetMapping("/{id}")
    @Operation(summary = "Obter lembrete por ID", description = "Retorna um lembrete de medicação específico do usuário autenticado")
    public ResponseEntity<LembreteMedicacaoDTO> getById(@PathVariable UUID id) {
        UUID usuarioId = getCurrentUserId();
        return ResponseEntity.ok(lembreteMedicacaoService.getByIdAndUsuario(id, usuarioId));
    }


    @PostMapping
    @Operation(summary = "Criar lembrete", description = "Cria um novo lembrete de medicação para o usuário autenticado")
    public ResponseEntity<LembreteMedicacaoDTO> create(@Valid @RequestBody LembreteMedicacaoRequest request) {
        UUID usuarioId = getCurrentUserId();
        LembreteMedicacaoDTO lembrete = lembreteMedicacaoService.create(usuarioId, request);
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(lembrete.getId())
                .toUri();
        
        return ResponseEntity.created(location).body(lembrete);
    }


    @PutMapping("/{id}")
    @Operation(summary = "Atualizar lembrete", description = "Atualiza um lembrete de medicação específico do usuário autenticado")
    public ResponseEntity<LembreteMedicacaoDTO> update(@PathVariable UUID id, @Valid @RequestBody LembreteMedicacaoRequest request) {
        UUID usuarioId = getCurrentUserId();
        return ResponseEntity.ok(lembreteMedicacaoService.update(id, usuarioId, request));
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir lembrete", description = "Exclui um lembrete de medicação específico do usuário autenticado")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID usuarioId = getCurrentUserId();
        lembreteMedicacaoService.delete(id, usuarioId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/hoje")
    @Operation(summary = "Listar lembretes para hoje", description = "Retorna todos os lembretes de medicação para hoje do usuário autenticado")
    public ResponseEntity<List<LembreteMedicacaoDTO>> getLembretesHoje() {
        UUID usuarioId = getCurrentUserId();
        return ResponseEntity.ok(lembreteMedicacaoService.getLembretesHoje(usuarioId));
    }

    @GetMapping("/ativos")
    @Operation(summary = "Listar lembretes ativos", description = "Retorna todos os lembretes de medicação ativos do usuário autenticado")
    public ResponseEntity<List<LembreteMedicacaoDTO>> getLembretesAtivos() {
        UUID usuarioId = getCurrentUserId();
        return ResponseEntity.ok(lembreteMedicacaoService.getLembretesAtivos(usuarioId));
    }


    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UsuarioDTO usuario = usuarioService.getByEmail(email);
        return usuario.getId();
    }
}
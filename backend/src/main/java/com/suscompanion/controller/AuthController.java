package com.suscompanion.controller;

import com.suscompanion.dto.auth.AuthResponse;
import com.suscompanion.dto.auth.LoginRequest;
import com.suscompanion.dto.auth.RefreshTokenRequest;
import com.suscompanion.dto.usuario.UsuarioRequest;
import com.suscompanion.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para autenticação de usuários")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Registrar novo usuário", description = "Cria um novo usuário e retorna tokens de autenticação")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário", description = "Autentica um usuário existente e retorna tokens de autenticação")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Renovar token", description = "Renova o token de acesso usando um refresh token válido")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout/{usuarioId}")
    @Operation(summary = "Encerrar sessão", description = "Revoga todos os refresh tokens do usuário")
    public ResponseEntity<Void> logout(@PathVariable UUID usuarioId) {
        authService.logout(usuarioId);
        return ResponseEntity.noContent().build();
    }
}
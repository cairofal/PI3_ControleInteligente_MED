package com.suscompanion.service;

import com.suscompanion.dto.auth.AuthResponse;
import com.suscompanion.dto.auth.LoginRequest;
import com.suscompanion.dto.auth.RefreshTokenRequest;
import com.suscompanion.dto.usuario.UsuarioRequest;
import com.suscompanion.model.RefreshToken;
import com.suscompanion.model.Usuario;
import com.suscompanion.repository.RefreshTokenRepository;
import com.suscompanion.repository.UsuarioRepository;
import com.suscompanion.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private UsuarioRequest usuarioRequest;
    private Usuario usuario;
    private RefreshToken refreshToken;
    private LoginRequest loginRequest;
    private RefreshTokenRequest refreshTokenRequest;

    @BeforeEach
    void setUp() {
        // Setup user request
        usuarioRequest = new UsuarioRequest();
        usuarioRequest.setNome("Teste Usuario");
        usuarioRequest.setEmail("teste@example.com");
        usuarioRequest.setCpf("12345678901");
        usuarioRequest.setDataNascimento(LocalDate.of(1990, 1, 1));
        usuarioRequest.setSenha("senha123");
        usuarioRequest.setTelefone("1234567890");

        // Setup user
        usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNome("Teste Usuario");
        usuario.setEmail("teste@example.com");
        usuario.setCpf("12345678901");
        usuario.setDataNascimento(LocalDate.of(1990, 1, 1));
        usuario.setSenha("encoded_senha123");
        usuario.setTelefone("1234567890");

        // Setup refresh token
        refreshToken = new RefreshToken();
        refreshToken.setId(UUID.randomUUID());
        refreshToken.setUsuario(usuario);
        refreshToken.setToken("refresh_token_value");
        refreshToken.setExpiracao(LocalDateTime.now().plusDays(7));
        refreshToken.setRevogado(false);

        // Setup login request
        loginRequest = new LoginRequest();
        loginRequest.setEmail("teste@example.com");
        loginRequest.setSenha("senha123");

        // Setup refresh token request
        refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setRefreshToken("refresh_token_value");
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        // Given
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository.existsByCpf(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_senha123");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(jwtService.generateToken(any())).thenReturn("access_token_value");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        // When
        AuthResponse response = authService.register(usuarioRequest);

        // Then
        assertNotNull(response);
        assertEquals("access_token_value", response.getAccessToken());
        assertEquals("refresh_token_value", response.getRefreshToken());
        assertEquals(usuario.getId().toString(), response.getUserId());
        assertEquals(usuario.getEmail(), response.getEmail());
        assertEquals(usuario.getNome(), response.getNome());

        verify(usuarioRepository).existsByEmail("teste@example.com");
        verify(usuarioRepository).existsByCpf("12345678901");
        verify(passwordEncoder).encode("senha123");
        verify(usuarioRepository).save(any(Usuario.class));
        verify(jwtService).generateToken(any());
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.register(usuarioRequest);
        });

        assertEquals("Email já cadastrado", exception.getMessage());
        verify(usuarioRepository).existsByEmail("teste@example.com");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void shouldLoginUserSuccessfully() {
        // Given
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));
        when(jwtService.generateToken(any())).thenReturn("access_token_value");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        // When
        AuthResponse response = authService.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals("access_token_value", response.getAccessToken());
        assertEquals("refresh_token_value", response.getRefreshToken());
        assertEquals(usuario.getId().toString(), response.getUserId());
        assertEquals(usuario.getEmail(), response.getEmail());
        assertEquals(usuario.getNome(), response.getNome());

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getSenha())
        );
        verify(usuarioRepository).findByEmail("teste@example.com");
        verify(jwtService).generateToken(any());
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void shouldRefreshTokenSuccessfully() {
        // Given
        when(refreshTokenRepository.findByToken(anyString())).thenReturn(Optional.of(refreshToken));
        when(jwtService.generateToken(any())).thenReturn("new_access_token_value");
        RefreshToken newRefreshToken = new RefreshToken();
        newRefreshToken.setToken("new_refresh_token_value");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(newRefreshToken);

        // When
        AuthResponse response = authService.refreshToken(refreshTokenRequest);

        // Then
        assertNotNull(response);
        assertEquals("new_access_token_value", response.getAccessToken());
        assertEquals("new_refresh_token_value", response.getRefreshToken());
        assertEquals(usuario.getId().toString(), response.getUserId());
        assertEquals(usuario.getEmail(), response.getEmail());
        assertEquals(usuario.getNome(), response.getNome());

        verify(refreshTokenRepository).findByToken("refresh_token_value");
        verify(jwtService).generateToken(any());
        verify(refreshTokenRepository, times(2)).save(any(RefreshToken.class));
    }

    @Test
    void shouldThrowExceptionWhenRefreshTokenIsInvalid() {
        // Given
        refreshToken.setRevogado(true);
        when(refreshTokenRepository.findByToken(anyString())).thenReturn(Optional.of(refreshToken));

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.refreshToken(refreshTokenRequest);
        });

        assertEquals("Refresh token expirado ou revogado", exception.getMessage());
        verify(refreshTokenRepository).findByToken("refresh_token_value");
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void shouldLogoutUserSuccessfully() {
        // Given
        UUID usuarioId = UUID.randomUUID();
        when(refreshTokenRepository.revokeAllByUsuarioId(any(UUID.class))).thenReturn(1);

        // When
        authService.logout(usuarioId);

        // Then
        verify(refreshTokenRepository).revokeAllByUsuarioId(usuarioId);
    }
}
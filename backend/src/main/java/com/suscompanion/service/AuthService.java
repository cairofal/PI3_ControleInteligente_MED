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
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

/**
 * Service for authentication operations.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Register a new user.
     * @param request the user registration request
     * @return the authentication response with tokens
     */
    @Transactional
    public AuthResponse register(UsuarioRequest request) {
        // Check if email already exists
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        // Check if CPF already exists (if provided)
        if (request.getCpf() != null && !request.getCpf().isEmpty() && usuarioRepository.existsByCpf(request.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }

        // Create new user
        var usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setCpf(request.getCpf());
        usuario.setDataNascimento(request.getDataNascimento());
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.setTelefone(request.getTelefone());

        // Save user
        usuario = usuarioRepository.save(usuario);

        // Create user details for token generation
        UserDetails userDetails = createUserDetails(usuario);

        // Generate tokens
        var accessToken = jwtService.generateToken(userDetails);
        var refreshToken = createRefreshToken(usuario);

        // Return authentication response
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .userId(usuario.getId().toString())
                .email(usuario.getEmail())
                .nome(usuario.getNome())
                .build();
    }

    /**
     * Authenticate a user.
     * @param request the login request
     * @return the authentication response with tokens
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        // Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getSenha()
                )
        );

        // Get user
        var usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        // Create user details for token generation
        UserDetails userDetails = createUserDetails(usuario);

        // Generate tokens
        var accessToken = jwtService.generateToken(userDetails);
        var refreshToken = createRefreshToken(usuario);

        // Return authentication response
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .userId(usuario.getId().toString())
                .email(usuario.getEmail())
                .nome(usuario.getNome())
                .build();
    }

    /**
     * Refresh an access token.
     * @param request the refresh token request
     * @return the authentication response with new tokens
     */
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        // Get refresh token
        var refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new IllegalArgumentException("Refresh token inválido"));

        // Check if token is valid
        if (!refreshToken.isValido()) {
            throw new IllegalArgumentException("Refresh token expirado ou revogado");
        }

        // Get user
        var usuario = refreshToken.getUsuario();

        // Create user details for token generation
        UserDetails userDetails = createUserDetails(usuario);

        // Generate new tokens
        var accessToken = jwtService.generateToken(userDetails);
        var newRefreshToken = createRefreshToken(usuario);

        // Revoke old refresh token
        refreshToken.setRevogado(true);
        refreshTokenRepository.save(refreshToken);

        // Return authentication response
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken.getToken())
                .userId(usuario.getId().toString())
                .email(usuario.getEmail())
                .nome(usuario.getNome())
                .build();
    }

    /**
     * Logout a user by revoking all refresh tokens.
     * @param usuarioId the user ID
     */
    @Transactional
    public void logout(UUID usuarioId) {
        refreshTokenRepository.revokeAllByUsuarioId(usuarioId);
    }

    /**
     * Create a refresh token for a user.
     * @param usuario the user
     * @return the created refresh token
     */
    private RefreshToken createRefreshToken(Usuario usuario) {
        var refreshToken = new RefreshToken();
        refreshToken.setUsuario(usuario);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiracao(LocalDateTime.now().plusDays(7)); // 7 days
        refreshToken.setRevogado(false);
        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Create user details for token generation.
     * @param usuario the user
     * @return the user details
     */
    private UserDetails createUserDetails(Usuario usuario) {
        return new User(
                usuario.getEmail(),
                usuario.getSenha(),
                Collections.emptyList()
        );
    }
}
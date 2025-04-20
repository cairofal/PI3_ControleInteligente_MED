package com.suscompanion.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for authentication response.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    /**
     * The access token.
     */
    private String accessToken;

    /**
     * The refresh token.
     */
    private String refreshToken;

    /**
     * The user ID.
     */
    private String userId;

    /**
     * The user email.
     */
    private String email;

    /**
     * The user name.
     */
    private String nome;
}
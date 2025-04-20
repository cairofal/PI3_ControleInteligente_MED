package com.suscompanion.service;

import com.suscompanion.dto.usuario.UsuarioDTO;
import com.suscompanion.dto.usuario.UsuarioRequest;
import com.suscompanion.exception.ResourceNotFoundException;
import com.suscompanion.model.Usuario;
import com.suscompanion.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service for user operations.
 */
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Get a user by ID.
     * @param id the user ID
     * @return the user DTO
     * @throws ResourceNotFoundException if the user is not found
     */
    @Transactional(readOnly = true)
    public UsuarioDTO getById(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forResource("Usuário", id));
        return modelMapper.map(usuario, UsuarioDTO.class);
    }

    /**
     * Get a user by email.
     * @param email the user email
     * @return the user DTO
     * @throws ResourceNotFoundException if the user is not found
     */
    @Transactional(readOnly = true)
    public UsuarioDTO getByEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> ResourceNotFoundException.forResourceWithField("Usuário", "email", email));
        return modelMapper.map(usuario, UsuarioDTO.class);
    }

    /**
     * Update a user.
     * @param id the user ID
     * @param request the user update request
     * @return the updated user DTO
     * @throws ResourceNotFoundException if the user is not found
     * @throws IllegalArgumentException if the email or CPF is already in use
     */
    @Transactional
    public UsuarioDTO update(UUID id, UsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forResource("Usuário", id));

        // Check if email is already in use by another user
        if (!usuario.getEmail().equals(request.getEmail()) && 
                usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        // Check if CPF is already in use by another user
        if (request.getCpf() != null && !request.getCpf().isEmpty() && 
                !request.getCpf().equals(usuario.getCpf()) && 
                usuarioRepository.existsByCpf(request.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }

        // Update user fields
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setCpf(request.getCpf());
        usuario.setDataNascimento(request.getDataNascimento());
        usuario.setTelefone(request.getTelefone());

        // Update password if provided
        if (request.getSenha() != null && !request.getSenha().isEmpty()) {
            usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        }

        // Save and return updated user
        usuario = usuarioRepository.save(usuario);
        return modelMapper.map(usuario, UsuarioDTO.class);
    }

    /**
     * Delete a user.
     * @param id the user ID
     * @throws ResourceNotFoundException if the user is not found
     */
    @Transactional
    public void delete(UUID id) {
        if (!usuarioRepository.existsById(id)) {
            throw ResourceNotFoundException.forResource("Usuário", id);
        }
        usuarioRepository.deleteById(id);
    }

    /**
     * Check if a user exists by ID.
     * @param id the user ID
     * @return true if the user exists
     */
    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        return usuarioRepository.existsById(id);
    }

    /**
     * Convert a Usuario entity to a UsuarioDTO.
     * @param usuario the Usuario entity
     * @return the UsuarioDTO
     */
    public UsuarioDTO toDTO(Usuario usuario) {
        return modelMapper.map(usuario, UsuarioDTO.class);
    }
}
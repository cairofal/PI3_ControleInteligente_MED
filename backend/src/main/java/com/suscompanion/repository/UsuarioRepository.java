package com.suscompanion.repository;

import com.suscompanion.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for accessing Usuario entities.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    /**
     * Find a user by email.
     * @param email the email to search for
     * @return an Optional containing the user if found
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Find a user by CPF.
     * @param cpf the CPF to search for
     * @return an Optional containing the user if found
     */
    Optional<Usuario> findByCpf(String cpf);

    /**
     * Check if a user exists with the given email.
     * @param email the email to check
     * @return true if a user exists with the email
     */
    boolean existsByEmail(String email);

    /**
     * Check if a user exists with the given CPF.
     * @param cpf the CPF to check
     * @return true if a user exists with the CPF
     */
    boolean existsByCpf(String cpf);
}
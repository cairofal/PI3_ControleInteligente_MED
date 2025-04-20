package com.suscompanion.service;

import com.suscompanion.dto.estoque.EstoquePessoalDTO;
import com.suscompanion.dto.estoque.EstoquePessoalRequest;
import com.suscompanion.exception.ResourceNotFoundException;
import com.suscompanion.model.EstoquePessoal;
import com.suscompanion.model.Medicamento;
import com.suscompanion.model.Usuario;
import com.suscompanion.repository.EstoquePessoalRepository;
import com.suscompanion.repository.MedicamentoRepository;
import com.suscompanion.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for personal inventory operations.
 */
@Service
@RequiredArgsConstructor
public class EstoquePessoalService {

    private final EstoquePessoalRepository estoquePessoalRepository;
    private final UsuarioRepository usuarioRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final ModelMapper modelMapper;

    /**
     * Get all inventory items for a user.
     * @param usuarioId the user ID
     * @param pageable pagination information
     * @return a page of inventory item DTOs
     */
    @Transactional(readOnly = true)
    public Page<EstoquePessoalDTO> getAllByUsuario(UUID usuarioId, Pageable pageable) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> ResourceNotFoundException.forResource("Usuário", usuarioId));
        return estoquePessoalRepository.findByUsuario(usuario, pageable)
                .map(this::toDTO);
    }

    /**
     * Get an inventory item by ID for a user.
     * @param id the inventory item ID
     * @param usuarioId the user ID
     * @return the inventory item DTO
     * @throws ResourceNotFoundException if the inventory item is not found
     */
    @Transactional(readOnly = true)
    public EstoquePessoalDTO getById(UUID id, UUID usuarioId) {
        EstoquePessoal estoque = estoquePessoalRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forResource("Estoque Pessoal", id));
        
        if (!estoque.getUsuario().getId().equals(usuarioId)) {
            throw ResourceNotFoundException.forResource("Estoque Pessoal", id);
        }
        
        return toDTO(estoque);
    }

    /**
     * Create a new inventory item for a user.
     * @param usuarioId the user ID
     * @param request the inventory item creation request
     * @return the created inventory item DTO
     * @throws ResourceNotFoundException if the user or medication is not found
     */
    @Transactional
    public EstoquePessoalDTO create(UUID usuarioId, EstoquePessoalRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> ResourceNotFoundException.forResource("Usuário", usuarioId));
        
        Medicamento medicamento = medicamentoRepository.findByIdAndUsuarioId(request.getMedicamentoId(), usuarioId)
                .orElseThrow(() -> ResourceNotFoundException.forResource("Medicamento", request.getMedicamentoId()));

        // Check if an inventory item already exists for this medication
        Optional<EstoquePessoal> existingEstoque = estoquePessoalRepository.findByUsuarioAndMedicamento(usuario, medicamento);
        if (existingEstoque.isPresent()) {
            // Update existing inventory item
            EstoquePessoal estoque = existingEstoque.get();
            estoque.setQuantidadeAtual(estoque.getQuantidadeAtual() + request.getQuantidadeAtual());
            estoque.setQuantidadeAlerta(request.getQuantidadeAlerta());
            return toDTO(estoquePessoalRepository.save(estoque));
        }

        // Create new inventory item
        EstoquePessoal estoque = new EstoquePessoal();
        estoque.setUsuario(usuario);
        estoque.setMedicamento(medicamento);
        estoque.setQuantidadeAtual(request.getQuantidadeAtual());
        estoque.setQuantidadeAlerta(request.getQuantidadeAlerta());

        estoque = estoquePessoalRepository.save(estoque);
        return toDTO(estoque);
    }

    /**
     * Update an inventory item for a user.
     * @param id the inventory item ID
     * @param usuarioId the user ID
     * @param request the inventory item update request
     * @return the updated inventory item DTO
     * @throws ResourceNotFoundException if the inventory item is not found
     */
    @Transactional
    public EstoquePessoalDTO update(UUID id, UUID usuarioId, EstoquePessoalRequest request) {
        EstoquePessoal estoque = estoquePessoalRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forResource("Estoque Pessoal", id));
        
        if (!estoque.getUsuario().getId().equals(usuarioId)) {
            throw ResourceNotFoundException.forResource("Estoque Pessoal", id);
        }
        
        // If medication ID is changing, verify the new medication exists
        if (!estoque.getMedicamento().getId().equals(request.getMedicamentoId())) {
            Medicamento medicamento = medicamentoRepository.findByIdAndUsuarioId(request.getMedicamentoId(), usuarioId)
                    .orElseThrow(() -> ResourceNotFoundException.forResource("Medicamento", request.getMedicamentoId()));
            estoque.setMedicamento(medicamento);
        }
        
        estoque.setQuantidadeAtual(request.getQuantidadeAtual());
        estoque.setQuantidadeAlerta(request.getQuantidadeAlerta());

        estoque = estoquePessoalRepository.save(estoque);
        return toDTO(estoque);
    }

    /**
     * Delete an inventory item for a user.
     * @param id the inventory item ID
     * @param usuarioId the user ID
     * @throws ResourceNotFoundException if the inventory item is not found
     */
    @Transactional
    public void delete(UUID id, UUID usuarioId) {
        EstoquePessoal estoque = estoquePessoalRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forResource("Estoque Pessoal", id));
        
        if (!estoque.getUsuario().getId().equals(usuarioId)) {
            throw ResourceNotFoundException.forResource("Estoque Pessoal", id);
        }
        
        estoquePessoalRepository.delete(estoque);
    }

    /**
     * Get inventory items with low stock for a user.
     * @param usuarioId the user ID
     * @return a list of inventory item DTOs with low stock
     */
    @Transactional(readOnly = true)
    public List<EstoquePessoalDTO> getEstoqueBaixo(UUID usuarioId) {
        List<EstoquePessoal> estoques = estoquePessoalRepository.findEstoqueBaixo(usuarioId);
        return estoques.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert an EstoquePessoal entity to an EstoquePessoalDTO.
     * @param estoque the EstoquePessoal entity
     * @return the EstoquePessoalDTO
     */
    private EstoquePessoalDTO toDTO(EstoquePessoal estoque) {
        EstoquePessoalDTO dto = modelMapper.map(estoque, EstoquePessoalDTO.class);
        dto.setUsuarioId(estoque.getUsuario().getId());
        dto.setEstoqueBaixo(estoque.isEstoqueBaixo());
        return dto;
    }
}
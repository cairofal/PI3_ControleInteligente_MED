package com.suscompanion.service;

import com.suscompanion.dto.medicamento.MedicamentoDTO;
import com.suscompanion.dto.medicamento.MedicamentoRequest;
import com.suscompanion.exception.ResourceNotFoundException;
import com.suscompanion.model.Medicamento;
import com.suscompanion.model.Usuario;
import com.suscompanion.repository.MedicamentoRepository;
import com.suscompanion.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for medication operations.
 */
@Service
@RequiredArgsConstructor
public class MedicamentoService {

    private final MedicamentoRepository medicamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper;

    /**
     * Get all medications for a user.
     * @param usuarioId the user ID
     * @param pageable pagination information
     * @return a page of medication DTOs
     */
    @Transactional(readOnly = true)
    public Page<MedicamentoDTO> getAllByUsuario(UUID usuarioId, Pageable pageable) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> ResourceNotFoundException.forResource("Usuário", usuarioId));
        return medicamentoRepository.findByUsuario(usuario, pageable)
                .map(medicamento -> modelMapper.map(medicamento, MedicamentoDTO.class));
    }

    /**
     * Get a medication by ID for a user.
     * @param id the medication ID
     * @param usuarioId the user ID
     * @return the medication DTO
     * @throws ResourceNotFoundException if the medication is not found
     */
    @Transactional(readOnly = true)
    public MedicamentoDTO getByIdAndUsuario(UUID id, UUID usuarioId) {
        Medicamento medicamento = medicamentoRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> ResourceNotFoundException.forResource("Medicamento", id));
        return modelMapper.map(medicamento, MedicamentoDTO.class);
    }

    /**
     * Create a new medication for a user.
     * @param usuarioId the user ID
     * @param request the medication creation request
     * @return the created medication DTO
     * @throws ResourceNotFoundException if the user is not found
     */
    @Transactional
    public MedicamentoDTO create(UUID usuarioId, MedicamentoRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> ResourceNotFoundException.forResource("Usuário", usuarioId));

        Medicamento medicamento = new Medicamento();
        medicamento.setNomeCompleto(request.getNomeCompleto());
        medicamento.setNomeSimplificado(request.getNomeSimplificado());
        medicamento.setDosagem(request.getDosagem());
        medicamento.setTipo(request.getTipo());
        medicamento.setFotoUrl(request.getFotoUrl());
        medicamento.setUsuario(usuario);

        medicamento = medicamentoRepository.save(medicamento);
        return modelMapper.map(medicamento, MedicamentoDTO.class);
    }

    /**
     * Update a medication for a user.
     * @param id the medication ID
     * @param usuarioId the user ID
     * @param request the medication update request
     * @return the updated medication DTO
     * @throws ResourceNotFoundException if the medication is not found
     */
    @Transactional
    public MedicamentoDTO update(UUID id, UUID usuarioId, MedicamentoRequest request) {
        Medicamento medicamento = medicamentoRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> ResourceNotFoundException.forResource("Medicamento", id));

        medicamento.setNomeCompleto(request.getNomeCompleto());
        medicamento.setNomeSimplificado(request.getNomeSimplificado());
        medicamento.setDosagem(request.getDosagem());
        medicamento.setTipo(request.getTipo());
        medicamento.setFotoUrl(request.getFotoUrl());

        medicamento = medicamentoRepository.save(medicamento);
        return modelMapper.map(medicamento, MedicamentoDTO.class);
    }

    /**
     * Delete a medication for a user.
     * @param id the medication ID
     * @param usuarioId the user ID
     * @throws ResourceNotFoundException if the medication is not found
     */
    @Transactional
    public void delete(UUID id, UUID usuarioId) {
        Medicamento medicamento = medicamentoRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> ResourceNotFoundException.forResource("Medicamento", id));
        medicamentoRepository.delete(medicamento);
    }

    /**
     * Search medications by name for a user.
     * @param nome the name to search for
     * @param usuarioId the user ID
     * @param pageable pagination information
     * @return a page of medication DTOs
     */
    @Transactional(readOnly = true)
    public Page<MedicamentoDTO> searchByNome(String nome, UUID usuarioId, Pageable pageable) {
        return medicamentoRepository.searchByNome(nome, usuarioId, pageable)
                .map(medicamento -> modelMapper.map(medicamento, MedicamentoDTO.class));
    }

    /**
     * Convert a Medicamento entity to a MedicamentoDTO.
     * @param medicamento the Medicamento entity
     * @return the MedicamentoDTO
     */
    public MedicamentoDTO toDTO(Medicamento medicamento) {
        return modelMapper.map(medicamento, MedicamentoDTO.class);
    }

    /**
     * Convert a list of Medicamento entities to a list of MedicamentoDTO.
     * @param medicamentos the list of Medicamento entities
     * @return the list of MedicamentoDTO
     */
    public List<MedicamentoDTO> toDTOList(List<Medicamento> medicamentos) {
        return medicamentos.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}

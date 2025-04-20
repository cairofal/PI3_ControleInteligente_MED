package com.suscompanion.service;

import com.suscompanion.dto.receita.ReceitaDTO;
import com.suscompanion.dto.receita.ReceitaItemDTO;
import com.suscompanion.dto.receita.ReceitaRequest;
import com.suscompanion.exception.ResourceNotFoundException;
import com.suscompanion.model.Medicamento;
import com.suscompanion.model.Receita;
import com.suscompanion.model.ReceitaItem;
import com.suscompanion.model.Usuario;
import com.suscompanion.repository.MedicamentoRepository;
import com.suscompanion.repository.ReceitaRepository;
import com.suscompanion.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for prescription operations.
 */
@Service
@RequiredArgsConstructor
public class ReceitaService {

    private final ReceitaRepository receitaRepository;
    private final UsuarioRepository usuarioRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final ModelMapper modelMapper;

    /**
     * Get all prescriptions for a user.
     * @param usuarioId the user ID
     * @param pageable pagination information
     * @return a page of prescription DTOs
     */
    @Transactional(readOnly = true)
    public Page<ReceitaDTO> getAllByUsuario(UUID usuarioId, Pageable pageable) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> ResourceNotFoundException.forResource("Usuário", usuarioId));
        return receitaRepository.findByUsuario(usuario, pageable)
                .map(this::toDTO);
    }

    /**
     * Get a prescription by ID for a user.
     * @param id the prescription ID
     * @param usuarioId the user ID
     * @return the prescription DTO
     * @throws ResourceNotFoundException if the prescription is not found
     */
    @Transactional(readOnly = true)
    public ReceitaDTO getByIdAndUsuario(UUID id, UUID usuarioId) {
        Receita receita = receitaRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> ResourceNotFoundException.forResource("Receita", id));
        return toDTO(receita);
    }

    /**
     * Create a new prescription for a user.
     * @param usuarioId the user ID
     * @param request the prescription creation request
     * @return the created prescription DTO
     * @throws ResourceNotFoundException if the user is not found
     */
    @Transactional
    public ReceitaDTO create(UUID usuarioId, ReceitaRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> ResourceNotFoundException.forResource("Usuário", usuarioId));

        Receita receita = new Receita();
        receita.setUsuario(usuario);
        receita.setMedicoNome(request.getMedicoNome());
        receita.setMedicoCrm(request.getMedicoCrm());
        receita.setDataEmissao(request.getDataEmissao());
        receita.setDataValidade(request.getDataValidade());
        receita.setObservacoes(request.getObservacoes());
        receita.setImagemUrl(request.getImagemUrl());
        receita.setItens(new ArrayList<>());

        // Save the prescription first to get an ID
        receita = receitaRepository.save(receita);

        // Add items to the prescription
        if (request.getItens() != null && !request.getItens().isEmpty()) {
            for (var itemRequest : request.getItens()) {
                ReceitaItem item = new ReceitaItem();
                item.setReceita(receita);
                item.setDescricao(itemRequest.getDescricao());
                item.setPosologia(itemRequest.getPosologia());
                item.setQuantidade(itemRequest.getQuantidade());

                // Set medication if provided
                if (itemRequest.getMedicamentoId() != null) {
                    Medicamento medicamento = medicamentoRepository.findByIdAndUsuarioId(itemRequest.getMedicamentoId(), usuarioId)
                            .orElseThrow(() -> ResourceNotFoundException.forResource("Medicamento", itemRequest.getMedicamentoId()));
                    item.setMedicamento(medicamento);
                }

                receita.addItem(item);
            }
            // Save again with items
            receita = receitaRepository.save(receita);
        }

        return toDTO(receita);
    }

    /**
     * Update a prescription for a user.
     * @param id the prescription ID
     * @param usuarioId the user ID
     * @param request the prescription update request
     * @return the updated prescription DTO
     * @throws ResourceNotFoundException if the prescription is not found
     */
    @Transactional
    public ReceitaDTO update(UUID id, UUID usuarioId, ReceitaRequest request) {
        Receita receita = receitaRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> ResourceNotFoundException.forResource("Receita", id));

        receita.setMedicoNome(request.getMedicoNome());
        receita.setMedicoCrm(request.getMedicoCrm());
        receita.setDataEmissao(request.getDataEmissao());
        receita.setDataValidade(request.getDataValidade());
        receita.setObservacoes(request.getObservacoes());
        receita.setImagemUrl(request.getImagemUrl());

        // Clear existing items and add new ones
        receita.getItens().clear();
        if (request.getItens() != null && !request.getItens().isEmpty()) {
            for (var itemRequest : request.getItens()) {
                ReceitaItem item = new ReceitaItem();
                item.setReceita(receita);
                item.setDescricao(itemRequest.getDescricao());
                item.setPosologia(itemRequest.getPosologia());
                item.setQuantidade(itemRequest.getQuantidade());

                // Set medication if provided
                if (itemRequest.getMedicamentoId() != null) {
                    Medicamento medicamento = medicamentoRepository.findByIdAndUsuarioId(itemRequest.getMedicamentoId(), usuarioId)
                            .orElseThrow(() -> ResourceNotFoundException.forResource("Medicamento", itemRequest.getMedicamentoId()));
                    item.setMedicamento(medicamento);
                }

                receita.addItem(item);
            }
        }

        receita = receitaRepository.save(receita);
        return toDTO(receita);
    }

    /**
     * Delete a prescription for a user.
     * @param id the prescription ID
     * @param usuarioId the user ID
     * @throws ResourceNotFoundException if the prescription is not found
     */
    @Transactional
    public void delete(UUID id, UUID usuarioId) {
        Receita receita = receitaRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> ResourceNotFoundException.forResource("Receita", id));
        receitaRepository.delete(receita);
    }

    /**
     * Get active prescriptions for a user.
     * @param usuarioId the user ID
     * @return a list of active prescription DTOs
     */
    @Transactional(readOnly = true)
    public List<ReceitaDTO> getReceitasAtivas(UUID usuarioId) {
        List<Receita> receitas = receitaRepository.findReceitasAtivas(usuarioId, LocalDate.now());
        return receitas.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search prescriptions by doctor name for a user.
     * @param medicoNome the doctor name to search for
     * @param usuarioId the user ID
     * @param pageable pagination information
     * @return a page of prescription DTOs
     */
    @Transactional(readOnly = true)
    public Page<ReceitaDTO> searchByMedicoNome(String medicoNome, UUID usuarioId, Pageable pageable) {
        return receitaRepository.searchByMedicoNome(medicoNome, usuarioId, pageable)
                .map(this::toDTO);
    }

    /**
     * Convert a Receita entity to a ReceitaDTO.
     * @param receita the Receita entity
     * @return the ReceitaDTO
     */
    private ReceitaDTO toDTO(Receita receita) {
        ReceitaDTO dto = modelMapper.map(receita, ReceitaDTO.class);
        dto.setUsuarioId(receita.getUsuario().getId());
        dto.setValida(receita.isValida());
        
        // Map items
        List<ReceitaItemDTO> itemDTOs = receita.getItens().stream()
                .map(item -> {
                    ReceitaItemDTO itemDTO = modelMapper.map(item, ReceitaItemDTO.class);
                    itemDTO.setReceitaId(receita.getId());
                    return itemDTO;
                })
                .collect(Collectors.toList());
        
        dto.setItens(itemDTOs);
        return dto;
    }
}
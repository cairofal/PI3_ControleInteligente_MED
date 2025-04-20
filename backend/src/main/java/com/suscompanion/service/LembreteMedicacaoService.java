package com.suscompanion.service;

import com.suscompanion.dto.lembrete.LembreteMedicacaoDTO;
import com.suscompanion.dto.lembrete.LembreteMedicacaoRequest;
import com.suscompanion.exception.ResourceNotFoundException;
import com.suscompanion.model.LembreteMedicacao;
import com.suscompanion.model.Medicamento;
import com.suscompanion.model.Usuario;
import com.suscompanion.repository.LembreteMedicacaoRepository;
import com.suscompanion.repository.MedicamentoRepository;
import com.suscompanion.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for medication reminder operations.
 */
@Service
@RequiredArgsConstructor
public class LembreteMedicacaoService {

    private final LembreteMedicacaoRepository lembreteMedicacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final ModelMapper modelMapper;

    /**
     * Get all medication reminders for a user.
     * @param usuarioId the user ID
     * @param pageable pagination information
     * @return a page of medication reminder DTOs
     */
    @Transactional(readOnly = true)
    public Page<LembreteMedicacaoDTO> getAllByUsuario(UUID usuarioId, Pageable pageable) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> ResourceNotFoundException.forResource("Usuário", usuarioId));
        return lembreteMedicacaoRepository.findByUsuario(usuario, pageable)
                .map(this::toDTO);
    }

    /**
     * Get a medication reminder by ID for a user.
     * @param id the medication reminder ID
     * @param usuarioId the user ID
     * @return the medication reminder DTO
     * @throws ResourceNotFoundException if the medication reminder is not found
     */
    @Transactional(readOnly = true)
    public LembreteMedicacaoDTO getByIdAndUsuario(UUID id, UUID usuarioId) {
        LembreteMedicacao lembrete = lembreteMedicacaoRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> ResourceNotFoundException.forResource("Lembrete de Medicação", id));
        return toDTO(lembrete);
    }

    /**
     * Create a new medication reminder for a user.
     * @param usuarioId the user ID
     * @param request the medication reminder creation request
     * @return the created medication reminder DTO
     * @throws ResourceNotFoundException if the user or medication is not found
     */
    @Transactional
    public LembreteMedicacaoDTO create(UUID usuarioId, LembreteMedicacaoRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> ResourceNotFoundException.forResource("Usuário", usuarioId));
        
        Medicamento medicamento = medicamentoRepository.findByIdAndUsuarioId(request.getMedicamentoId(), usuarioId)
                .orElseThrow(() -> ResourceNotFoundException.forResource("Medicamento", request.getMedicamentoId()));

        LembreteMedicacao lembrete = new LembreteMedicacao();
        lembrete.setUsuario(usuario);
        lembrete.setMedicamento(medicamento);
        lembrete.setHorarios(request.getHorarios());
        lembrete.setDiasSemana(request.getDiasSemana());
        lembrete.setQuantidadeDose(BigDecimal.valueOf(request.getQuantidadeDose()));
        lembrete.setInstrucoes(request.getInstrucoes());
        lembrete.setAtivo(request.getAtivo() != null ? request.getAtivo() : true);

        lembrete = lembreteMedicacaoRepository.save(lembrete);
        return toDTO(lembrete);
    }

    /**
     * Update a medication reminder for a user.
     * @param id the medication reminder ID
     * @param usuarioId the user ID
     * @param request the medication reminder update request
     * @return the updated medication reminder DTO
     * @throws ResourceNotFoundException if the medication reminder or medication is not found
     */
    @Transactional
    public LembreteMedicacaoDTO update(UUID id, UUID usuarioId, LembreteMedicacaoRequest request) {
        LembreteMedicacao lembrete = lembreteMedicacaoRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> ResourceNotFoundException.forResource("Lembrete de Medicação", id));
        
        Medicamento medicamento = medicamentoRepository.findByIdAndUsuarioId(request.getMedicamentoId(), usuarioId)
                .orElseThrow(() -> ResourceNotFoundException.forResource("Medicamento", request.getMedicamentoId()));

        lembrete.setMedicamento(medicamento);
        lembrete.setHorarios(request.getHorarios());
        lembrete.setDiasSemana(request.getDiasSemana());
        lembrete.setQuantidadeDose(BigDecimal.valueOf(request.getQuantidadeDose()));
        lembrete.setInstrucoes(request.getInstrucoes());
        lembrete.setAtivo(request.getAtivo() != null ? request.getAtivo() : lembrete.getAtivo());

        lembrete = lembreteMedicacaoRepository.save(lembrete);
        return toDTO(lembrete);
    }

    /**
     * Delete a medication reminder for a user.
     * @param id the medication reminder ID
     * @param usuarioId the user ID
     * @throws ResourceNotFoundException if the medication reminder is not found
     */
    @Transactional
    public void delete(UUID id, UUID usuarioId) {
        LembreteMedicacao lembrete = lembreteMedicacaoRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> ResourceNotFoundException.forResource("Lembrete de Medicação", id));
        lembreteMedicacaoRepository.delete(lembrete);
    }

    /**
     * Get medication reminders for today for a user.
     * @param usuarioId the user ID
     * @return a list of medication reminder DTOs for today
     */
    @Transactional(readOnly = true)
    public List<LembreteMedicacaoDTO> getLembretesHoje(UUID usuarioId) {
        int diaSemana = LocalDateTime.now().getDayOfWeek().getValue() % 7; // 0-6 (Sunday-Saturday)
        List<LembreteMedicacao> lembretes = lembreteMedicacaoRepository.findLembretesHoje(usuarioId, diaSemana);
        return lembretes.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get active medication reminders for a user.
     * @param usuarioId the user ID
     * @return a list of active medication reminder DTOs
     */
    @Transactional(readOnly = true)
    public List<LembreteMedicacaoDTO> getLembretesAtivos(UUID usuarioId) {
        List<LembreteMedicacao> lembretes = lembreteMedicacaoRepository.findByUsuarioIdAndAtivoTrue(usuarioId);
        return lembretes.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert a LembreteMedicacao entity to a LembreteMedicacaoDTO.
     * @param lembrete the LembreteMedicacao entity
     * @return the LembreteMedicacaoDTO
     */
    private LembreteMedicacaoDTO toDTO(LembreteMedicacao lembrete) {
        LembreteMedicacaoDTO dto = modelMapper.map(lembrete, LembreteMedicacaoDTO.class);
        dto.setUsuarioId(lembrete.getUsuario().getId());
        dto.setParaHoje(lembrete.isParaHoje());
        return dto;
    }
}
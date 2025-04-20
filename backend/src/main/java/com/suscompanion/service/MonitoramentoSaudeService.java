package com.suscompanion.service;

import com.suscompanion.dto.saude.MonitoramentoSaudeDTO;
import com.suscompanion.dto.saude.MonitoramentoSaudeRequest;
import com.suscompanion.exception.ResourceNotFoundException;
import com.suscompanion.model.MonitoramentoSaude;
import com.suscompanion.model.MonitoramentoSaude.TipoMonitoramento;
import com.suscompanion.model.Usuario;
import com.suscompanion.repository.MonitoramentoSaudeRepository;
import com.suscompanion.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for health monitoring operations.
 */
@Service
@RequiredArgsConstructor
public class MonitoramentoSaudeService {

    private final MonitoramentoSaudeRepository monitoramentoSaudeRepository;
    private final UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper;

    /**
     * Get all health monitoring records for a user.
     * @param usuarioId the user ID
     * @param pageable pagination information
     * @return a page of health monitoring record DTOs
     */
    @Transactional(readOnly = true)
    public Page<MonitoramentoSaudeDTO> getAllByUsuario(UUID usuarioId, Pageable pageable) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> ResourceNotFoundException.forResource("Usuário", usuarioId));
        return monitoramentoSaudeRepository.findByUsuario(usuario, pageable)
                .map(this::toDTO);
    }

    /**
     * Get a health monitoring record by ID for a user.
     * @param id the health monitoring record ID
     * @param usuarioId the user ID
     * @return the health monitoring record DTO
     * @throws ResourceNotFoundException if the health monitoring record is not found
     */
    @Transactional(readOnly = true)
    public MonitoramentoSaudeDTO getByIdAndUsuario(UUID id, UUID usuarioId) {
        MonitoramentoSaude monitoramento = monitoramentoSaudeRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> ResourceNotFoundException.forResource("Monitoramento de Saúde", id));
        return toDTO(monitoramento);
    }

    /**
     * Create a new health monitoring record for a user.
     * @param usuarioId the user ID
     * @param request the health monitoring record creation request
     * @return the created health monitoring record DTO
     * @throws ResourceNotFoundException if the user is not found
     * @throws IllegalArgumentException if the request is invalid
     */
    @Transactional
    public MonitoramentoSaudeDTO create(UUID usuarioId, MonitoramentoSaudeRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> ResourceNotFoundException.forResource("Usuário", usuarioId));

        validateRequest(request);

        MonitoramentoSaude monitoramento = new MonitoramentoSaude();
        monitoramento.setUsuario(usuario);
        monitoramento.setTipo(request.getTipo());
        monitoramento.setDataRegistro(request.getDataRegistro() != null ? request.getDataRegistro() : LocalDateTime.now());
        monitoramento.setObservacoes(request.getObservacoes());

        // Set type-specific fields
        if (request.getTipo() == TipoMonitoramento.PRESSAO) {
            monitoramento.setValorSistolica(request.getValorSistolica());
            monitoramento.setValorDiastolica(request.getValorDiastolica());
            monitoramento.setPulsacao(request.getPulsacao());
        } else if (request.getTipo() == TipoMonitoramento.GLICEMIA) {
            monitoramento.setValorGlicemia(request.getValorGlicemia());
            monitoramento.setJejum(request.getJejum());
        }

        monitoramento = monitoramentoSaudeRepository.save(monitoramento);
        return toDTO(monitoramento);
    }

    /**
     * Update a health monitoring record for a user.
     * @param id the health monitoring record ID
     * @param usuarioId the user ID
     * @param request the health monitoring record update request
     * @return the updated health monitoring record DTO
     * @throws ResourceNotFoundException if the health monitoring record is not found
     * @throws IllegalArgumentException if the request is invalid
     */
    @Transactional
    public MonitoramentoSaudeDTO update(UUID id, UUID usuarioId, MonitoramentoSaudeRequest request) {
        MonitoramentoSaude monitoramento = monitoramentoSaudeRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> ResourceNotFoundException.forResource("Monitoramento de Saúde", id));

        validateRequest(request);

        // Update common fields
        monitoramento.setTipo(request.getTipo());
        monitoramento.setDataRegistro(request.getDataRegistro() != null ? request.getDataRegistro() : monitoramento.getDataRegistro());
        monitoramento.setObservacoes(request.getObservacoes());

        // Update type-specific fields
        if (request.getTipo() == TipoMonitoramento.PRESSAO) {
            monitoramento.setValorSistolica(request.getValorSistolica());
            monitoramento.setValorDiastolica(request.getValorDiastolica());
            monitoramento.setPulsacao(request.getPulsacao());
            monitoramento.setValorGlicemia(null);
            monitoramento.setJejum(null);
        } else if (request.getTipo() == TipoMonitoramento.GLICEMIA) {
            monitoramento.setValorGlicemia(request.getValorGlicemia());
            monitoramento.setJejum(request.getJejum());
            monitoramento.setValorSistolica(null);
            monitoramento.setValorDiastolica(null);
            monitoramento.setPulsacao(null);
        }

        monitoramento = monitoramentoSaudeRepository.save(monitoramento);
        return toDTO(monitoramento);
    }

    /**
     * Delete a health monitoring record for a user.
     * @param id the health monitoring record ID
     * @param usuarioId the user ID
     * @throws ResourceNotFoundException if the health monitoring record is not found
     */
    @Transactional
    public void delete(UUID id, UUID usuarioId) {
        MonitoramentoSaude monitoramento = monitoramentoSaudeRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> ResourceNotFoundException.forResource("Monitoramento de Saúde", id));
        monitoramentoSaudeRepository.delete(monitoramento);
    }

    /**
     * Get health monitoring records by type for a user.
     * @param tipo the type of health monitoring
     * @param usuarioId the user ID
     * @param pageable pagination information
     * @return a page of health monitoring record DTOs
     */
    @Transactional(readOnly = true)
    public Page<MonitoramentoSaudeDTO> getByTipo(TipoMonitoramento tipo, UUID usuarioId, Pageable pageable) {
        return monitoramentoSaudeRepository.findByTipoAndUsuarioId(tipo, usuarioId, pageable)
                .map(this::toDTO);
    }

    /**
     * Get the latest health monitoring records for a user.
     * @param usuarioId the user ID
     * @param limit the maximum number of records to return
     * @return a list of the latest health monitoring record DTOs
     */
    @Transactional(readOnly = true)
    public List<MonitoramentoSaudeDTO> getUltimosRegistros(UUID usuarioId, int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "dataRegistro"));
        List<MonitoramentoSaude> monitoramentos = monitoramentoSaudeRepository.findUltimosRegistros(usuarioId, pageable);
        return monitoramentos.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get health monitoring records within a date range for a user.
     * @param usuarioId the user ID
     * @param inicio the start date
     * @param fim the end date
     * @param pageable pagination information
     * @return a page of health monitoring record DTOs within the date range
     */
    @Transactional(readOnly = true)
    public Page<MonitoramentoSaudeDTO> getByDataRegistroBetween(UUID usuarioId, LocalDateTime inicio, LocalDateTime fim, Pageable pageable) {
        return monitoramentoSaudeRepository.findByUsuarioIdAndDataRegistroBetween(usuarioId, inicio, fim, pageable)
                .map(this::toDTO);
    }

    /**
     * Validate a health monitoring record request.
     * @param request the health monitoring record request
     * @throws IllegalArgumentException if the request is invalid
     */
    private void validateRequest(MonitoramentoSaudeRequest request) {
        if (request.getTipo() == TipoMonitoramento.PRESSAO) {
            if (request.getValorSistolica() == null || request.getValorDiastolica() == null) {
                throw new IllegalArgumentException("Valores de pressão sistólica e diastólica são obrigatórios para monitoramento de pressão");
            }
        } else if (request.getTipo() == TipoMonitoramento.GLICEMIA) {
            if (request.getValorGlicemia() == null) {
                throw new IllegalArgumentException("Valor de glicemia é obrigatório para monitoramento de glicemia");
            }
        }
    }

    /**
     * Convert a MonitoramentoSaude entity to a MonitoramentoSaudeDTO.
     * @param monitoramento the MonitoramentoSaude entity
     * @return the MonitoramentoSaudeDTO
     */
    private MonitoramentoSaudeDTO toDTO(MonitoramentoSaude monitoramento) {
        MonitoramentoSaudeDTO dto = modelMapper.map(monitoramento, MonitoramentoSaudeDTO.class);
        dto.setUsuarioId(monitoramento.getUsuario().getId());
        return dto;
    }
}
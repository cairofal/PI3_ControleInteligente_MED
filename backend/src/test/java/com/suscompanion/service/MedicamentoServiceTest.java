package com.suscompanion.service;

import com.suscompanion.dto.medicamento.MedicamentoDTO;
import com.suscompanion.dto.medicamento.MedicamentoRequest;
import com.suscompanion.exception.ResourceNotFoundException;
import com.suscompanion.model.Medicamento;
import com.suscompanion.model.Usuario;
import com.suscompanion.repository.MedicamentoRepository;
import com.suscompanion.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicamentoServiceTest {

    @Mock
    private MedicamentoRepository medicamentoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private MedicamentoService medicamentoService;

    private Usuario usuario;
    private Medicamento medicamento;
    private MedicamentoDTO medicamentoDTO;
    private MedicamentoRequest medicamentoRequest;
    private UUID usuarioId;
    private UUID medicamentoId;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        medicamentoId = UUID.randomUUID();
        
        // Setup user
        usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setNome("Teste Usuario");
        usuario.setEmail("teste@example.com");
        
        // Setup medication
        medicamento = new Medicamento();
        medicamento.setId(medicamentoId);
        medicamento.setNomeCompleto("Paracetamol 500mg");
        medicamento.setNomeSimplificado("Paracetamol");
        medicamento.setDosagem("500mg");
        medicamento.setTipo("Comprimido");
        medicamento.setUsuario(usuario);
        medicamento.setCriadoEm(LocalDateTime.now());
        medicamento.setAtualizadoEm(LocalDateTime.now());
        
        // Setup medication DTO
        medicamentoDTO = new MedicamentoDTO();
        medicamentoDTO.setId(medicamentoId);
        medicamentoDTO.setNomeCompleto("Paracetamol 500mg");
        medicamentoDTO.setNomeSimplificado("Paracetamol");
        medicamentoDTO.setDosagem("500mg");
        medicamentoDTO.setTipo("Comprimido");
        medicamentoDTO.setCriadoEm(LocalDateTime.now());
        medicamentoDTO.setAtualizadoEm(LocalDateTime.now());
        
        // Setup medication request
        medicamentoRequest = new MedicamentoRequest();
        medicamentoRequest.setNomeCompleto("Paracetamol 500mg");
        medicamentoRequest.setNomeSimplificado("Paracetamol");
        medicamentoRequest.setDosagem("500mg");
        medicamentoRequest.setTipo("Comprimido");
        
        // Setup pageable
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void shouldGetAllMedicationsByUsuario() {
        // Given
        List<Medicamento> medicamentos = Arrays.asList(medicamento);
        Page<Medicamento> medicamentoPage = new PageImpl<>(medicamentos, pageable, medicamentos.size());
        
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(medicamentoRepository.findByUsuario(usuario, pageable)).thenReturn(medicamentoPage);
        when(modelMapper.map(medicamento, MedicamentoDTO.class)).thenReturn(medicamentoDTO);
        
        // When
        Page<MedicamentoDTO> result = medicamentoService.getAllByUsuario(usuarioId, pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(medicamentoDTO, result.getContent().get(0));
        
        verify(usuarioRepository).findById(usuarioId);
        verify(medicamentoRepository).findByUsuario(usuario, pageable);
        verify(modelMapper).map(medicamento, MedicamentoDTO.class);
    }

    @Test
    void shouldGetMedicationByIdAndUsuario() {
        // Given
        when(medicamentoRepository.findByIdAndUsuarioId(medicamentoId, usuarioId)).thenReturn(Optional.of(medicamento));
        when(modelMapper.map(medicamento, MedicamentoDTO.class)).thenReturn(medicamentoDTO);
        
        // When
        MedicamentoDTO result = medicamentoService.getByIdAndUsuario(medicamentoId, usuarioId);
        
        // Then
        assertNotNull(result);
        assertEquals(medicamentoDTO, result);
        
        verify(medicamentoRepository).findByIdAndUsuarioId(medicamentoId, usuarioId);
        verify(modelMapper).map(medicamento, MedicamentoDTO.class);
    }

    @Test
    void shouldThrowExceptionWhenMedicationNotFound() {
        // Given
        when(medicamentoRepository.findByIdAndUsuarioId(medicamentoId, usuarioId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            medicamentoService.getByIdAndUsuario(medicamentoId, usuarioId);
        });
        
        verify(medicamentoRepository).findByIdAndUsuarioId(medicamentoId, usuarioId);
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void shouldCreateMedication() {
        // Given
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(medicamentoRepository.save(any(Medicamento.class))).thenReturn(medicamento);
        when(modelMapper.map(medicamento, MedicamentoDTO.class)).thenReturn(medicamentoDTO);
        
        // When
        MedicamentoDTO result = medicamentoService.create(usuarioId, medicamentoRequest);
        
        // Then
        assertNotNull(result);
        assertEquals(medicamentoDTO, result);
        
        verify(usuarioRepository).findById(usuarioId);
        verify(medicamentoRepository).save(any(Medicamento.class));
        verify(modelMapper).map(medicamento, MedicamentoDTO.class);
    }

    @Test
    void shouldUpdateMedication() {
        // Given
        when(medicamentoRepository.findByIdAndUsuarioId(medicamentoId, usuarioId)).thenReturn(Optional.of(medicamento));
        when(medicamentoRepository.save(medicamento)).thenReturn(medicamento);
        when(modelMapper.map(medicamento, MedicamentoDTO.class)).thenReturn(medicamentoDTO);
        
        // When
        MedicamentoDTO result = medicamentoService.update(medicamentoId, usuarioId, medicamentoRequest);
        
        // Then
        assertNotNull(result);
        assertEquals(medicamentoDTO, result);
        
        verify(medicamentoRepository).findByIdAndUsuarioId(medicamentoId, usuarioId);
        verify(medicamentoRepository).save(medicamento);
        verify(modelMapper).map(medicamento, MedicamentoDTO.class);
    }

    @Test
    void shouldDeleteMedication() {
        // Given
        when(medicamentoRepository.findByIdAndUsuarioId(medicamentoId, usuarioId)).thenReturn(Optional.of(medicamento));
        
        // When
        medicamentoService.delete(medicamentoId, usuarioId);
        
        // Then
        verify(medicamentoRepository).findByIdAndUsuarioId(medicamentoId, usuarioId);
        verify(medicamentoRepository).delete(medicamento);
    }

    @Test
    void shouldSearchMedicationsByNome() {
        // Given
        String searchTerm = "Para";
        List<Medicamento> medicamentos = Arrays.asList(medicamento);
        Page<Medicamento> medicamentoPage = new PageImpl<>(medicamentos, pageable, medicamentos.size());
        
        when(medicamentoRepository.searchByNome(searchTerm, usuarioId, pageable)).thenReturn(medicamentoPage);
        when(modelMapper.map(medicamento, MedicamentoDTO.class)).thenReturn(medicamentoDTO);
        
        // When
        Page<MedicamentoDTO> result = medicamentoService.searchByNome(searchTerm, usuarioId, pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(medicamentoDTO, result.getContent().get(0));
        
        verify(medicamentoRepository).searchByNome(searchTerm, usuarioId, pageable);
        verify(modelMapper).map(medicamento, MedicamentoDTO.class);
    }
}
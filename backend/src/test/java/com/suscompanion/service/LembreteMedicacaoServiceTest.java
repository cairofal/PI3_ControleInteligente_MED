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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LembreteMedicacaoServiceTest {

    @Mock
    private LembreteMedicacaoRepository lembreteMedicacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private MedicamentoRepository medicamentoRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private LembreteMedicacaoService lembreteMedicacaoService;

    private Usuario usuario;
    private Medicamento medicamento;
    private LembreteMedicacao lembrete;
    private LembreteMedicacaoDTO lembreteDTO;
    private LembreteMedicacaoRequest lembreteRequest;
    private UUID usuarioId;
    private UUID medicamentoId;
    private UUID lembreteId;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        medicamentoId = UUID.randomUUID();
        lembreteId = UUID.randomUUID();
        
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
        
        // Setup reminder
        lembrete = new LembreteMedicacao();
        lembrete.setId(lembreteId);
        lembrete.setUsuario(usuario);
        lembrete.setMedicamento(medicamento);
        lembrete.setHorarios(Arrays.asList(LocalTime.of(8, 0), LocalTime.of(20, 0)));
        lembrete.setDiasSemana(Arrays.asList(1, 2, 3, 4, 5)); // Monday to Friday
        lembrete.setQuantidadeDose(BigDecimal.valueOf(1.0));
        lembrete.setInstrucoes("Tomar com água");
        lembrete.setAtivo(true);
        lembrete.setCriadoEm(LocalDateTime.now());
        
        // Setup reminder DTO
        lembreteDTO = new LembreteMedicacaoDTO();
        lembreteDTO.setId(lembreteId);
        lembreteDTO.setUsuarioId(usuarioId);
        lembreteDTO.setHorarios(Arrays.asList(LocalTime.of(8, 0), LocalTime.of(20, 0)));
        lembreteDTO.setDiasSemana(Arrays.asList(1, 2, 3, 4, 5));
        lembreteDTO.setQuantidadeDose(1.0);
        lembreteDTO.setInstrucoes("Tomar com água");
        lembreteDTO.setAtivo(true);
        lembreteDTO.setCriadoEm(LocalDateTime.now());
        lembreteDTO.setParaHoje(true);
        
        // Setup reminder request
        lembreteRequest = new LembreteMedicacaoRequest();
        lembreteRequest.setMedicamentoId(medicamentoId);
        lembreteRequest.setHorarios(Arrays.asList(LocalTime.of(8, 0), LocalTime.of(20, 0)));
        lembreteRequest.setDiasSemana(Arrays.asList(1, 2, 3, 4, 5));
        lembreteRequest.setQuantidadeDose(1.0);
        lembreteRequest.setInstrucoes("Tomar com água");
        lembreteRequest.setAtivo(true);
        
        // Setup pageable
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void shouldGetAllRemindersByUsuario() {
        // Given
        List<LembreteMedicacao> lembretes = Arrays.asList(lembrete);
        Page<LembreteMedicacao> lembretePage = new PageImpl<>(lembretes, pageable, lembretes.size());
        
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(lembreteMedicacaoRepository.findByUsuario(usuario, pageable)).thenReturn(lembretePage);
        when(modelMapper.map(lembrete, LembreteMedicacaoDTO.class)).thenReturn(lembreteDTO);
        
        // When
        Page<LembreteMedicacaoDTO> result = lembreteMedicacaoService.getAllByUsuario(usuarioId, pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(lembreteDTO, result.getContent().get(0));
        
        verify(usuarioRepository).findById(usuarioId);
        verify(lembreteMedicacaoRepository).findByUsuario(usuario, pageable);
        verify(modelMapper).map(lembrete, LembreteMedicacaoDTO.class);
    }

    @Test
    void shouldGetReminderByIdAndUsuario() {
        // Given
        when(lembreteMedicacaoRepository.findByIdAndUsuarioId(lembreteId, usuarioId)).thenReturn(Optional.of(lembrete));
        when(modelMapper.map(lembrete, LembreteMedicacaoDTO.class)).thenReturn(lembreteDTO);
        
        // When
        LembreteMedicacaoDTO result = lembreteMedicacaoService.getByIdAndUsuario(lembreteId, usuarioId);
        
        // Then
        assertNotNull(result);
        assertEquals(lembreteDTO, result);
        
        verify(lembreteMedicacaoRepository).findByIdAndUsuarioId(lembreteId, usuarioId);
        verify(modelMapper).map(lembrete, LembreteMedicacaoDTO.class);
    }

    @Test
    void shouldThrowExceptionWhenReminderNotFound() {
        // Given
        when(lembreteMedicacaoRepository.findByIdAndUsuarioId(lembreteId, usuarioId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            lembreteMedicacaoService.getByIdAndUsuario(lembreteId, usuarioId);
        });
        
        verify(lembreteMedicacaoRepository).findByIdAndUsuarioId(lembreteId, usuarioId);
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void shouldCreateReminder() {
        // Given
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(medicamentoRepository.findByIdAndUsuarioId(medicamentoId, usuarioId)).thenReturn(Optional.of(medicamento));
        when(lembreteMedicacaoRepository.save(any(LembreteMedicacao.class))).thenReturn(lembrete);
        when(modelMapper.map(lembrete, LembreteMedicacaoDTO.class)).thenReturn(lembreteDTO);
        
        // When
        LembreteMedicacaoDTO result = lembreteMedicacaoService.create(usuarioId, lembreteRequest);
        
        // Then
        assertNotNull(result);
        assertEquals(lembreteDTO, result);
        
        verify(usuarioRepository).findById(usuarioId);
        verify(medicamentoRepository).findByIdAndUsuarioId(medicamentoId, usuarioId);
        verify(lembreteMedicacaoRepository).save(any(LembreteMedicacao.class));
        verify(modelMapper).map(lembrete, LembreteMedicacaoDTO.class);
    }

    @Test
    void shouldUpdateReminder() {
        // Given
        when(lembreteMedicacaoRepository.findByIdAndUsuarioId(lembreteId, usuarioId)).thenReturn(Optional.of(lembrete));
        when(medicamentoRepository.findByIdAndUsuarioId(medicamentoId, usuarioId)).thenReturn(Optional.of(medicamento));
        when(lembreteMedicacaoRepository.save(lembrete)).thenReturn(lembrete);
        when(modelMapper.map(lembrete, LembreteMedicacaoDTO.class)).thenReturn(lembreteDTO);
        
        // When
        LembreteMedicacaoDTO result = lembreteMedicacaoService.update(lembreteId, usuarioId, lembreteRequest);
        
        // Then
        assertNotNull(result);
        assertEquals(lembreteDTO, result);
        
        verify(lembreteMedicacaoRepository).findByIdAndUsuarioId(lembreteId, usuarioId);
        verify(medicamentoRepository).findByIdAndUsuarioId(medicamentoId, usuarioId);
        verify(lembreteMedicacaoRepository).save(lembrete);
        verify(modelMapper).map(lembrete, LembreteMedicacaoDTO.class);
    }

    @Test
    void shouldDeleteReminder() {
        // Given
        when(lembreteMedicacaoRepository.findByIdAndUsuarioId(lembreteId, usuarioId)).thenReturn(Optional.of(lembrete));
        
        // When
        lembreteMedicacaoService.delete(lembreteId, usuarioId);
        
        // Then
        verify(lembreteMedicacaoRepository).findByIdAndUsuarioId(lembreteId, usuarioId);
        verify(lembreteMedicacaoRepository).delete(lembrete);
    }

    @Test
    void shouldGetRemindersForToday() {
        // Given
        List<LembreteMedicacao> lembretes = Arrays.asList(lembrete);
        int diaSemana = LocalDateTime.now().getDayOfWeek().getValue() % 7; // 0-6 (Sunday-Saturday)
        
        when(lembreteMedicacaoRepository.findLembretesHoje(usuarioId, diaSemana)).thenReturn(lembretes);
        when(modelMapper.map(lembrete, LembreteMedicacaoDTO.class)).thenReturn(lembreteDTO);
        
        // When
        List<LembreteMedicacaoDTO> result = lembreteMedicacaoService.getLembretesHoje(usuarioId);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(lembreteDTO, result.get(0));
        
        verify(lembreteMedicacaoRepository).findLembretesHoje(eq(usuarioId), anyInt());
        verify(modelMapper).map(lembrete, LembreteMedicacaoDTO.class);
    }

    @Test
    void shouldGetActiveReminders() {
        // Given
        List<LembreteMedicacao> lembretes = Arrays.asList(lembrete);
        
        when(lembreteMedicacaoRepository.findByUsuarioIdAndAtivoTrue(usuarioId)).thenReturn(lembretes);
        when(modelMapper.map(lembrete, LembreteMedicacaoDTO.class)).thenReturn(lembreteDTO);
        
        // When
        List<LembreteMedicacaoDTO> result = lembreteMedicacaoService.getLembretesAtivos(usuarioId);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(lembreteDTO, result.get(0));
        
        verify(lembreteMedicacaoRepository).findByUsuarioIdAndAtivoTrue(usuarioId);
        verify(modelMapper).map(lembrete, LembreteMedicacaoDTO.class);
    }
}
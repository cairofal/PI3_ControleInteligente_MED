package com.suscompanion.service;

import com.suscompanion.dto.saude.MonitoramentoSaudeDTO;
import com.suscompanion.dto.saude.MonitoramentoSaudeRequest;
import com.suscompanion.exception.ResourceNotFoundException;
import com.suscompanion.model.MonitoramentoSaude;
import com.suscompanion.model.MonitoramentoSaude.TipoMonitoramento;
import com.suscompanion.model.Usuario;
import com.suscompanion.repository.MonitoramentoSaudeRepository;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MonitoramentoSaudeServiceTest {

    @Mock
    private MonitoramentoSaudeRepository monitoramentoSaudeRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private MonitoramentoSaudeService monitoramentoSaudeService;

    private Usuario usuario;
    private MonitoramentoSaude monitoramentoPressao;
    private MonitoramentoSaude monitoramentoGlicemia;
    private MonitoramentoSaudeDTO monitoramentoPressaoDTO;
    private MonitoramentoSaudeDTO monitoramentoGlicemiaDTO;
    private MonitoramentoSaudeRequest requestPressao;
    private MonitoramentoSaudeRequest requestGlicemia;
    private UUID usuarioId;
    private UUID monitoramentoPressaoId;
    private UUID monitoramentoGlicemiaId;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        monitoramentoPressaoId = UUID.randomUUID();
        monitoramentoGlicemiaId = UUID.randomUUID();

        // Setup user
        usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setNome("Teste Usuario");
        usuario.setEmail("teste@example.com");

        // Setup blood pressure monitoring
        monitoramentoPressao = new MonitoramentoSaude();
        monitoramentoPressao.setId(monitoramentoPressaoId);
        monitoramentoPressao.setUsuario(usuario);
        monitoramentoPressao.setTipo(TipoMonitoramento.PRESSAO);
        monitoramentoPressao.setValorSistolica(120);
        monitoramentoPressao.setValorDiastolica(80);
        monitoramentoPressao.setPulsacao(70);
        monitoramentoPressao.setObservacoes("Pressão normal");
        monitoramentoPressao.setDataRegistro(LocalDateTime.now().minusHours(2));
        monitoramentoPressao.setCriadoEm(LocalDateTime.now().minusHours(2));

        // Setup blood glucose monitoring
        monitoramentoGlicemia = new MonitoramentoSaude();
        monitoramentoGlicemia.setId(monitoramentoGlicemiaId);
        monitoramentoGlicemia.setUsuario(usuario);
        monitoramentoGlicemia.setTipo(TipoMonitoramento.GLICEMIA);
        monitoramentoGlicemia.setValorGlicemia(BigDecimal.valueOf(100.0));
        monitoramentoGlicemia.setJejum(true);
        monitoramentoGlicemia.setObservacoes("Glicemia em jejum");
        monitoramentoGlicemia.setDataRegistro(LocalDateTime.now().minusHours(1));
        monitoramentoGlicemia.setCriadoEm(LocalDateTime.now().minusHours(1));

        // Setup blood pressure monitoring DTO
        monitoramentoPressaoDTO = new MonitoramentoSaudeDTO();
        monitoramentoPressaoDTO.setId(monitoramentoPressaoId);
        monitoramentoPressaoDTO.setUsuarioId(usuarioId);
        monitoramentoPressaoDTO.setTipo(TipoMonitoramento.PRESSAO);
        monitoramentoPressaoDTO.setValorSistolica(120);
        monitoramentoPressaoDTO.setValorDiastolica(80);
        monitoramentoPressaoDTO.setPulsacao(70);
        monitoramentoPressaoDTO.setObservacoes("Pressão normal");
        monitoramentoPressaoDTO.setDataRegistro(LocalDateTime.now().minusHours(2));
        monitoramentoPressaoDTO.setCriadoEm(LocalDateTime.now().minusHours(2));

        // Setup blood glucose monitoring DTO
        monitoramentoGlicemiaDTO = new MonitoramentoSaudeDTO();
        monitoramentoGlicemiaDTO.setId(monitoramentoGlicemiaId);
        monitoramentoGlicemiaDTO.setUsuarioId(usuarioId);
        monitoramentoGlicemiaDTO.setTipo(TipoMonitoramento.GLICEMIA);
        monitoramentoGlicemiaDTO.setValorGlicemia(BigDecimal.valueOf(100.0));
        monitoramentoGlicemiaDTO.setJejum(true);
        monitoramentoGlicemiaDTO.setObservacoes("Glicemia em jejum");
        monitoramentoGlicemiaDTO.setDataRegistro(LocalDateTime.now().minusHours(1));
        monitoramentoGlicemiaDTO.setCriadoEm(LocalDateTime.now().minusHours(1));

        // Setup blood pressure monitoring request
        requestPressao = new MonitoramentoSaudeRequest();
        requestPressao.setTipo(TipoMonitoramento.PRESSAO);
        requestPressao.setValorSistolica(120);
        requestPressao.setValorDiastolica(80);
        requestPressao.setPulsacao(70);
        requestPressao.setObservacoes("Pressão normal");
        requestPressao.setDataRegistro(LocalDateTime.now().minusHours(2));

        // Setup blood glucose monitoring request
        requestGlicemia = new MonitoramentoSaudeRequest();
        requestGlicemia.setTipo(TipoMonitoramento.GLICEMIA);
        requestGlicemia.setValorGlicemia(BigDecimal.valueOf(100.0));
        requestGlicemia.setJejum(true);
        requestGlicemia.setObservacoes("Glicemia em jejum");
        requestGlicemia.setDataRegistro(LocalDateTime.now().minusHours(1));

        // Setup pageable
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void shouldGetAllHealthMonitoringRecordsByUsuario() {
        // Given
        List<MonitoramentoSaude> monitoramentos = Arrays.asList(monitoramentoPressao, monitoramentoGlicemia);
        Page<MonitoramentoSaude> monitoramentoPage = new PageImpl<>(monitoramentos, pageable, monitoramentos.size());

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(monitoramentoSaudeRepository.findByUsuario(usuario, pageable)).thenReturn(monitoramentoPage);
        when(modelMapper.map(monitoramentoPressao, MonitoramentoSaudeDTO.class)).thenReturn(monitoramentoPressaoDTO);
        when(modelMapper.map(monitoramentoGlicemia, MonitoramentoSaudeDTO.class)).thenReturn(monitoramentoGlicemiaDTO);

        // When
        Page<MonitoramentoSaudeDTO> result = monitoramentoSaudeService.getAllByUsuario(usuarioId, pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());

        verify(usuarioRepository).findById(usuarioId);
        verify(monitoramentoSaudeRepository).findByUsuario(usuario, pageable);
        verify(modelMapper).map(monitoramentoPressao, MonitoramentoSaudeDTO.class);
        verify(modelMapper).map(monitoramentoGlicemia, MonitoramentoSaudeDTO.class);
    }

    @Test
    void shouldGetHealthMonitoringRecordByIdAndUsuario() {
        // Given
        when(monitoramentoSaudeRepository.findByIdAndUsuarioId(monitoramentoPressaoId, usuarioId)).thenReturn(Optional.of(monitoramentoPressao));
        when(modelMapper.map(monitoramentoPressao, MonitoramentoSaudeDTO.class)).thenReturn(monitoramentoPressaoDTO);

        // When
        MonitoramentoSaudeDTO result = monitoramentoSaudeService.getByIdAndUsuario(monitoramentoPressaoId, usuarioId);

        // Then
        assertNotNull(result);
        assertEquals(monitoramentoPressaoDTO, result);

        verify(monitoramentoSaudeRepository).findByIdAndUsuarioId(monitoramentoPressaoId, usuarioId);
        verify(modelMapper).map(monitoramentoPressao, MonitoramentoSaudeDTO.class);
    }

    @Test
    void shouldThrowExceptionWhenHealthMonitoringRecordNotFound() {
        // Given
        when(monitoramentoSaudeRepository.findByIdAndUsuarioId(monitoramentoPressaoId, usuarioId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            monitoramentoSaudeService.getByIdAndUsuario(monitoramentoPressaoId, usuarioId);
        });

        verify(monitoramentoSaudeRepository).findByIdAndUsuarioId(monitoramentoPressaoId, usuarioId);
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void shouldCreateBloodPressureMonitoring() {
        // Given
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(monitoramentoSaudeRepository.save(any(MonitoramentoSaude.class))).thenReturn(monitoramentoPressao);
        when(modelMapper.map(monitoramentoPressao, MonitoramentoSaudeDTO.class)).thenReturn(monitoramentoPressaoDTO);

        // When
        MonitoramentoSaudeDTO result = monitoramentoSaudeService.create(usuarioId, requestPressao);

        // Then
        assertNotNull(result);
        assertEquals(monitoramentoPressaoDTO, result);

        verify(usuarioRepository).findById(usuarioId);
        verify(monitoramentoSaudeRepository).save(any(MonitoramentoSaude.class));
        verify(modelMapper).map(monitoramentoPressao, MonitoramentoSaudeDTO.class);
    }

    @Test
    void shouldCreateBloodGlucoseMonitoring() {
        // Given
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(monitoramentoSaudeRepository.save(any(MonitoramentoSaude.class))).thenReturn(monitoramentoGlicemia);
        when(modelMapper.map(monitoramentoGlicemia, MonitoramentoSaudeDTO.class)).thenReturn(monitoramentoGlicemiaDTO);

        // When
        MonitoramentoSaudeDTO result = monitoramentoSaudeService.create(usuarioId, requestGlicemia);

        // Then
        assertNotNull(result);
        assertEquals(monitoramentoGlicemiaDTO, result);

        verify(usuarioRepository).findById(usuarioId);
        verify(monitoramentoSaudeRepository).save(any(MonitoramentoSaude.class));
        verify(modelMapper).map(monitoramentoGlicemia, MonitoramentoSaudeDTO.class);
    }

    @Test
    void shouldThrowExceptionWhenCreatingInvalidBloodPressureMonitoring() {
        // Given
        MonitoramentoSaudeRequest invalidRequest = new MonitoramentoSaudeRequest();
        invalidRequest.setTipo(TipoMonitoramento.PRESSAO);
        // Missing required fields: valorSistolica and valorDiastolica

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            monitoramentoSaudeService.create(usuarioId, invalidRequest);
        });

        verify(usuarioRepository).findById(usuarioId);
        verify(monitoramentoSaudeRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenCreatingInvalidBloodGlucoseMonitoring() {
        // Given
        MonitoramentoSaudeRequest invalidRequest = new MonitoramentoSaudeRequest();
        invalidRequest.setTipo(TipoMonitoramento.GLICEMIA);
        // Missing required field: valorGlicemia

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            monitoramentoSaudeService.create(usuarioId, invalidRequest);
        });

        verify(usuarioRepository).findById(usuarioId);
        verify(monitoramentoSaudeRepository, never()).save(any());
    }

    @Test
    void shouldUpdateHealthMonitoringRecord() {
        // Given
        when(monitoramentoSaudeRepository.findByIdAndUsuarioId(monitoramentoPressaoId, usuarioId)).thenReturn(Optional.of(monitoramentoPressao));
        when(monitoramentoSaudeRepository.save(monitoramentoPressao)).thenReturn(monitoramentoPressao);
        when(modelMapper.map(monitoramentoPressao, MonitoramentoSaudeDTO.class)).thenReturn(monitoramentoPressaoDTO);

        // When
        MonitoramentoSaudeDTO result = monitoramentoSaudeService.update(monitoramentoPressaoId, usuarioId, requestPressao);

        // Then
        assertNotNull(result);
        assertEquals(monitoramentoPressaoDTO, result);

        verify(monitoramentoSaudeRepository).findByIdAndUsuarioId(monitoramentoPressaoId, usuarioId);
        verify(monitoramentoSaudeRepository).save(monitoramentoPressao);
        verify(modelMapper).map(monitoramentoPressao, MonitoramentoSaudeDTO.class);
    }

    @Test
    void shouldUpdateHealthMonitoringRecordType() {
        // Given
        when(monitoramentoSaudeRepository.findByIdAndUsuarioId(monitoramentoPressaoId, usuarioId)).thenReturn(Optional.of(monitoramentoPressao));
        when(monitoramentoSaudeRepository.save(monitoramentoPressao)).thenReturn(monitoramentoGlicemia);
        when(modelMapper.map(monitoramentoGlicemia, MonitoramentoSaudeDTO.class)).thenReturn(monitoramentoGlicemiaDTO);

        // When
        MonitoramentoSaudeDTO result = monitoramentoSaudeService.update(monitoramentoPressaoId, usuarioId, requestGlicemia);

        // Then
        assertNotNull(result);
        assertEquals(monitoramentoGlicemiaDTO, result);

        verify(monitoramentoSaudeRepository).findByIdAndUsuarioId(monitoramentoPressaoId, usuarioId);
        verify(monitoramentoSaudeRepository).save(monitoramentoPressao);
        verify(modelMapper).map(monitoramentoGlicemia, MonitoramentoSaudeDTO.class);
    }

    @Test
    void shouldDeleteHealthMonitoringRecord() {
        // Given
        when(monitoramentoSaudeRepository.findByIdAndUsuarioId(monitoramentoPressaoId, usuarioId)).thenReturn(Optional.of(monitoramentoPressao));

        // When
        monitoramentoSaudeService.delete(monitoramentoPressaoId, usuarioId);

        // Then
        verify(monitoramentoSaudeRepository).findByIdAndUsuarioId(monitoramentoPressaoId, usuarioId);
        verify(monitoramentoSaudeRepository).delete(monitoramentoPressao);
    }

    @Test
    void shouldGetHealthMonitoringRecordsByType() {
        // Given
        List<MonitoramentoSaude> monitoramentos = Arrays.asList(monitoramentoPressao);
        Page<MonitoramentoSaude> monitoramentoPage = new PageImpl<>(monitoramentos, pageable, monitoramentos.size());

        when(monitoramentoSaudeRepository.findByTipoAndUsuarioId(TipoMonitoramento.PRESSAO, usuarioId, pageable)).thenReturn(monitoramentoPage);
        when(modelMapper.map(monitoramentoPressao, MonitoramentoSaudeDTO.class)).thenReturn(monitoramentoPressaoDTO);

        // When
        Page<MonitoramentoSaudeDTO> result = monitoramentoSaudeService.getByTipo(TipoMonitoramento.PRESSAO, usuarioId, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(monitoramentoPressaoDTO, result.getContent().get(0));

        verify(monitoramentoSaudeRepository).findByTipoAndUsuarioId(TipoMonitoramento.PRESSAO, usuarioId, pageable);
        verify(modelMapper).map(monitoramentoPressao, MonitoramentoSaudeDTO.class);
    }

    @Test
    void shouldGetLatestHealthMonitoringRecords() {
        // Given
        List<MonitoramentoSaude> monitoramentos = Arrays.asList(monitoramentoGlicemia, monitoramentoPressao);

        when(monitoramentoSaudeRepository.findUltimosRegistros(eq(usuarioId), any(Pageable.class))).thenReturn(monitoramentos);
        when(modelMapper.map(monitoramentoGlicemia, MonitoramentoSaudeDTO.class)).thenReturn(monitoramentoGlicemiaDTO);
        when(modelMapper.map(monitoramentoPressao, MonitoramentoSaudeDTO.class)).thenReturn(monitoramentoPressaoDTO);

        // When
        List<MonitoramentoSaudeDTO> result = monitoramentoSaudeService.getUltimosRegistros(usuarioId, 5);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        verify(monitoramentoSaudeRepository).findUltimosRegistros(eq(usuarioId), any(Pageable.class));
        verify(modelMapper).map(monitoramentoGlicemia, MonitoramentoSaudeDTO.class);
        verify(modelMapper).map(monitoramentoPressao, MonitoramentoSaudeDTO.class);
    }

    @Test
    void shouldGetHealthMonitoringRecordsByDateRange() {
        // Given
        List<MonitoramentoSaude> monitoramentos = Arrays.asList(monitoramentoPressao, monitoramentoGlicemia);
        Page<MonitoramentoSaude> monitoramentoPage = new PageImpl<>(monitoramentos, pageable, monitoramentos.size());

        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = LocalDateTime.now();

        when(monitoramentoSaudeRepository.findByUsuarioIdAndDataRegistroBetween(usuarioId, inicio, fim, pageable)).thenReturn(monitoramentoPage);
        when(modelMapper.map(monitoramentoPressao, MonitoramentoSaudeDTO.class)).thenReturn(monitoramentoPressaoDTO);
        when(modelMapper.map(monitoramentoGlicemia, MonitoramentoSaudeDTO.class)).thenReturn(monitoramentoGlicemiaDTO);

        // When
        Page<MonitoramentoSaudeDTO> result = monitoramentoSaudeService.getByDataRegistroBetween(usuarioId, inicio, fim, pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());

        verify(monitoramentoSaudeRepository).findByUsuarioIdAndDataRegistroBetween(usuarioId, inicio, fim, pageable);
        verify(modelMapper).map(monitoramentoPressao, MonitoramentoSaudeDTO.class);
        verify(modelMapper).map(monitoramentoGlicemia, MonitoramentoSaudeDTO.class);
    }
}

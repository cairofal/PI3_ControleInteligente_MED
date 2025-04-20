package com.suscompanion.service;

import com.suscompanion.dto.receita.ReceitaDTO;
import com.suscompanion.dto.receita.ReceitaItemDTO;
import com.suscompanion.dto.receita.ReceitaRequest;
import com.suscompanion.dto.receita.ReceitaItemRequest;
import com.suscompanion.exception.ResourceNotFoundException;
import com.suscompanion.model.Medicamento;
import com.suscompanion.model.Receita;
import com.suscompanion.model.ReceitaItem;
import com.suscompanion.model.Usuario;
import com.suscompanion.repository.MedicamentoRepository;
import com.suscompanion.repository.ReceitaRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReceitaServiceTest {

    @Mock
    private ReceitaRepository receitaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private MedicamentoRepository medicamentoRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ReceitaService receitaService;

    private Usuario usuario;
    private Medicamento medicamento;
    private Receita receita;
    private ReceitaItem receitaItem;
    private ReceitaDTO receitaDTO;
    private ReceitaItemDTO receitaItemDTO;
    private ReceitaRequest receitaRequest;
    private ReceitaItemRequest receitaItemRequest;
    private UUID usuarioId;
    private UUID receitaId;
    private UUID medicamentoId;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        receitaId = UUID.randomUUID();
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
        
        // Setup prescription item
        receitaItem = new ReceitaItem();
        receitaItem.setId(UUID.randomUUID());
        receitaItem.setMedicamento(medicamento);
        receitaItem.setDescricao("Tomar em caso de dor");
        receitaItem.setPosologia("1 comprimido a cada 8 horas");
        receitaItem.setQuantidade(20);
        
        // Setup prescription
        receita = new Receita();
        receita.setId(receitaId);
        receita.setUsuario(usuario);
        receita.setMedicoNome("Dr. Teste");
        receita.setMedicoCrm("12345");
        receita.setDataEmissao(LocalDate.now().minusDays(5));
        receita.setDataValidade(LocalDate.now().plusMonths(6));
        receita.setObservacoes("Observações de teste");
        receita.setItens(new ArrayList<>(Arrays.asList(receitaItem)));
        receita.setCriadoEm(LocalDateTime.now());
        receitaItem.setReceita(receita);
        
        // Setup prescription item DTO
        receitaItemDTO = new ReceitaItemDTO();
        receitaItemDTO.setId(receitaItem.getId());
        receitaItemDTO.setReceitaId(receitaId);
        receitaItemDTO.setDescricao("Tomar em caso de dor");
        receitaItemDTO.setPosologia("1 comprimido a cada 8 horas");
        receitaItemDTO.setQuantidade(20);
        
        // Setup prescription DTO
        receitaDTO = new ReceitaDTO();
        receitaDTO.setId(receitaId);
        receitaDTO.setUsuarioId(usuarioId);
        receitaDTO.setMedicoNome("Dr. Teste");
        receitaDTO.setMedicoCrm("12345");
        receitaDTO.setDataEmissao(LocalDate.now().minusDays(5));
        receitaDTO.setDataValidade(LocalDate.now().plusMonths(6));
        receitaDTO.setObservacoes("Observações de teste");
        receitaDTO.setItens(new ArrayList<>(Arrays.asList(receitaItemDTO)));
        receitaDTO.setCriadoEm(LocalDateTime.now());
        receitaDTO.setValida(true);
        
        // Setup prescription item request
        receitaItemRequest = new ReceitaItemRequest();
        receitaItemRequest.setMedicamentoId(medicamentoId);
        receitaItemRequest.setDescricao("Tomar em caso de dor");
        receitaItemRequest.setPosologia("1 comprimido a cada 8 horas");
        receitaItemRequest.setQuantidade(20);
        
        // Setup prescription request
        receitaRequest = new ReceitaRequest();
        receitaRequest.setMedicoNome("Dr. Teste");
        receitaRequest.setMedicoCrm("12345");
        receitaRequest.setDataEmissao(LocalDate.now().minusDays(5));
        receitaRequest.setDataValidade(LocalDate.now().plusMonths(6));
        receitaRequest.setObservacoes("Observações de teste");
        receitaRequest.setItens(new ArrayList<>(Arrays.asList(receitaItemRequest)));
        
        // Setup pageable
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void shouldGetAllPrescriptionsByUsuario() {
        // Given
        List<Receita> receitas = Arrays.asList(receita);
        Page<Receita> receitaPage = new PageImpl<>(receitas, pageable, receitas.size());
        
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(receitaRepository.findByUsuario(usuario, pageable)).thenReturn(receitaPage);
        when(modelMapper.map(receita, ReceitaDTO.class)).thenReturn(receitaDTO);
        
        // When
        Page<ReceitaDTO> result = receitaService.getAllByUsuario(usuarioId, pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(receitaDTO, result.getContent().get(0));
        
        verify(usuarioRepository).findById(usuarioId);
        verify(receitaRepository).findByUsuario(usuario, pageable);
        verify(modelMapper).map(receita, ReceitaDTO.class);
    }

    @Test
    void shouldGetPrescriptionByIdAndUsuario() {
        // Given
        when(receitaRepository.findByIdAndUsuarioId(receitaId, usuarioId)).thenReturn(Optional.of(receita));
        when(modelMapper.map(receita, ReceitaDTO.class)).thenReturn(receitaDTO);
        
        // When
        ReceitaDTO result = receitaService.getByIdAndUsuario(receitaId, usuarioId);
        
        // Then
        assertNotNull(result);
        assertEquals(receitaDTO, result);
        
        verify(receitaRepository).findByIdAndUsuarioId(receitaId, usuarioId);
        verify(modelMapper).map(receita, ReceitaDTO.class);
    }

    @Test
    void shouldThrowExceptionWhenPrescriptionNotFound() {
        // Given
        when(receitaRepository.findByIdAndUsuarioId(receitaId, usuarioId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            receitaService.getByIdAndUsuario(receitaId, usuarioId);
        });
        
        verify(receitaRepository).findByIdAndUsuarioId(receitaId, usuarioId);
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void shouldCreatePrescription() {
        // Given
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(medicamentoRepository.findByIdAndUsuarioId(medicamentoId, usuarioId)).thenReturn(Optional.of(medicamento));
        when(receitaRepository.save(any(Receita.class))).thenReturn(receita);
        when(modelMapper.map(receita, ReceitaDTO.class)).thenReturn(receitaDTO);
        
        // When
        ReceitaDTO result = receitaService.create(usuarioId, receitaRequest);
        
        // Then
        assertNotNull(result);
        assertEquals(receitaDTO, result);
        
        verify(usuarioRepository).findById(usuarioId);
        verify(medicamentoRepository).findByIdAndUsuarioId(medicamentoId, usuarioId);
        verify(receitaRepository, times(2)).save(any(Receita.class));
        verify(modelMapper).map(receita, ReceitaDTO.class);
    }

    @Test
    void shouldUpdatePrescription() {
        // Given
        when(receitaRepository.findByIdAndUsuarioId(receitaId, usuarioId)).thenReturn(Optional.of(receita));
        when(medicamentoRepository.findByIdAndUsuarioId(medicamentoId, usuarioId)).thenReturn(Optional.of(medicamento));
        when(receitaRepository.save(receita)).thenReturn(receita);
        when(modelMapper.map(receita, ReceitaDTO.class)).thenReturn(receitaDTO);
        
        // When
        ReceitaDTO result = receitaService.update(receitaId, usuarioId, receitaRequest);
        
        // Then
        assertNotNull(result);
        assertEquals(receitaDTO, result);
        
        verify(receitaRepository).findByIdAndUsuarioId(receitaId, usuarioId);
        verify(medicamentoRepository).findByIdAndUsuarioId(medicamentoId, usuarioId);
        verify(receitaRepository).save(receita);
        verify(modelMapper).map(receita, ReceitaDTO.class);
    }

    @Test
    void shouldDeletePrescription() {
        // Given
        when(receitaRepository.findByIdAndUsuarioId(receitaId, usuarioId)).thenReturn(Optional.of(receita));
        
        // When
        receitaService.delete(receitaId, usuarioId);
        
        // Then
        verify(receitaRepository).findByIdAndUsuarioId(receitaId, usuarioId);
        verify(receitaRepository).delete(receita);
    }

    @Test
    void shouldGetActivePrescriptions() {
        // Given
        List<Receita> receitas = Arrays.asList(receita);
        
        when(receitaRepository.findReceitasAtivas(eq(usuarioId), any(LocalDate.class))).thenReturn(receitas);
        when(modelMapper.map(receita, ReceitaDTO.class)).thenReturn(receitaDTO);
        
        // When
        List<ReceitaDTO> result = receitaService.getReceitasAtivas(usuarioId);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(receitaDTO, result.get(0));
        
        verify(receitaRepository).findReceitasAtivas(eq(usuarioId), any(LocalDate.class));
        verify(modelMapper).map(receita, ReceitaDTO.class);
    }

    @Test
    void shouldSearchPrescriptionsByMedicoNome() {
        // Given
        String searchTerm = "Dr. Teste";
        List<Receita> receitas = Arrays.asList(receita);
        Page<Receita> receitaPage = new PageImpl<>(receitas, pageable, receitas.size());
        
        when(receitaRepository.searchByMedicoNome(searchTerm, usuarioId, pageable)).thenReturn(receitaPage);
        when(modelMapper.map(receita, ReceitaDTO.class)).thenReturn(receitaDTO);
        
        // When
        Page<ReceitaDTO> result = receitaService.searchByMedicoNome(searchTerm, usuarioId, pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(receitaDTO, result.getContent().get(0));
        
        verify(receitaRepository).searchByMedicoNome(searchTerm, usuarioId, pageable);
        verify(modelMapper).map(receita, ReceitaDTO.class);
    }
}
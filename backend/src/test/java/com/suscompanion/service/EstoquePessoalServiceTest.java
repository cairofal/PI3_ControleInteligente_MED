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
class EstoquePessoalServiceTest {

    @Mock
    private EstoquePessoalRepository estoquePessoalRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private MedicamentoRepository medicamentoRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private EstoquePessoalService estoquePessoalService;

    private Usuario usuario;
    private Medicamento medicamento;
    private EstoquePessoal estoque;
    private EstoquePessoalDTO estoqueDTO;
    private EstoquePessoalRequest estoqueRequest;
    private UUID usuarioId;
    private UUID medicamentoId;
    private UUID estoqueId;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        medicamentoId = UUID.randomUUID();
        estoqueId = UUID.randomUUID();
        
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
        
        // Setup inventory
        estoque = new EstoquePessoal();
        estoque.setId(estoqueId);
        estoque.setUsuario(usuario);
        estoque.setMedicamento(medicamento);
        estoque.setQuantidadeAtual(20);
        estoque.setQuantidadeAlerta(5);
        estoque.setCriadoEm(LocalDateTime.now());
        estoque.setAtualizadoEm(LocalDateTime.now());
        
        // Setup inventory DTO
        estoqueDTO = new EstoquePessoalDTO();
        estoqueDTO.setId(estoqueId);
        estoqueDTO.setUsuarioId(usuarioId);
        estoqueDTO.setQuantidadeAtual(20);
        estoqueDTO.setQuantidadeAlerta(5);
        estoqueDTO.setCriadoEm(LocalDateTime.now());
        estoqueDTO.setAtualizadoEm(LocalDateTime.now());
        estoqueDTO.setEstoqueBaixo(false);
        
        // Setup inventory request
        estoqueRequest = new EstoquePessoalRequest();
        estoqueRequest.setMedicamentoId(medicamentoId);
        estoqueRequest.setQuantidadeAtual(20);
        estoqueRequest.setQuantidadeAlerta(5);
        
        // Setup pageable
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void shouldGetAllInventoryItemsByUsuario() {
        // Given
        List<EstoquePessoal> estoques = Arrays.asList(estoque);
        Page<EstoquePessoal> estoquePage = new PageImpl<>(estoques, pageable, estoques.size());
        
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(estoquePessoalRepository.findByUsuario(usuario, pageable)).thenReturn(estoquePage);
        when(modelMapper.map(estoque, EstoquePessoalDTO.class)).thenReturn(estoqueDTO);
        
        // When
        Page<EstoquePessoalDTO> result = estoquePessoalService.getAllByUsuario(usuarioId, pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(estoqueDTO, result.getContent().get(0));
        
        verify(usuarioRepository).findById(usuarioId);
        verify(estoquePessoalRepository).findByUsuario(usuario, pageable);
        verify(modelMapper).map(estoque, EstoquePessoalDTO.class);
    }

    @Test
    void shouldGetInventoryItemById() {
        // Given
        when(estoquePessoalRepository.findById(estoqueId)).thenReturn(Optional.of(estoque));
        when(modelMapper.map(estoque, EstoquePessoalDTO.class)).thenReturn(estoqueDTO);
        
        // When
        EstoquePessoalDTO result = estoquePessoalService.getById(estoqueId, usuarioId);
        
        // Then
        assertNotNull(result);
        assertEquals(estoqueDTO, result);
        
        verify(estoquePessoalRepository).findById(estoqueId);
        verify(modelMapper).map(estoque, EstoquePessoalDTO.class);
    }

    @Test
    void shouldThrowExceptionWhenInventoryItemNotFound() {
        // Given
        when(estoquePessoalRepository.findById(estoqueId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            estoquePessoalService.getById(estoqueId, usuarioId);
        });
        
        verify(estoquePessoalRepository).findById(estoqueId);
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void shouldThrowExceptionWhenInventoryItemBelongsToAnotherUser() {
        // Given
        UUID anotherUserId = UUID.randomUUID();
        Usuario anotherUsuario = new Usuario();
        anotherUsuario.setId(anotherUserId);
        
        EstoquePessoal estoqueWithDifferentUser = new EstoquePessoal();
        estoqueWithDifferentUser.setId(estoqueId);
        estoqueWithDifferentUser.setUsuario(anotherUsuario);
        
        when(estoquePessoalRepository.findById(estoqueId)).thenReturn(Optional.of(estoqueWithDifferentUser));
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            estoquePessoalService.getById(estoqueId, usuarioId);
        });
        
        verify(estoquePessoalRepository).findById(estoqueId);
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void shouldCreateInventoryItem() {
        // Given
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(medicamentoRepository.findByIdAndUsuarioId(medicamentoId, usuarioId)).thenReturn(Optional.of(medicamento));
        when(estoquePessoalRepository.findByUsuarioAndMedicamento(usuario, medicamento)).thenReturn(Optional.empty());
        when(estoquePessoalRepository.save(any(EstoquePessoal.class))).thenReturn(estoque);
        when(modelMapper.map(estoque, EstoquePessoalDTO.class)).thenReturn(estoqueDTO);
        
        // When
        EstoquePessoalDTO result = estoquePessoalService.create(usuarioId, estoqueRequest);
        
        // Then
        assertNotNull(result);
        assertEquals(estoqueDTO, result);
        
        verify(usuarioRepository).findById(usuarioId);
        verify(medicamentoRepository).findByIdAndUsuarioId(medicamentoId, usuarioId);
        verify(estoquePessoalRepository).findByUsuarioAndMedicamento(usuario, medicamento);
        verify(estoquePessoalRepository).save(any(EstoquePessoal.class));
        verify(modelMapper).map(estoque, EstoquePessoalDTO.class);
    }

    @Test
    void shouldUpdateExistingInventoryItemWhenCreatingWithSameMedicamento() {
        // Given
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(medicamentoRepository.findByIdAndUsuarioId(medicamentoId, usuarioId)).thenReturn(Optional.of(medicamento));
        when(estoquePessoalRepository.findByUsuarioAndMedicamento(usuario, medicamento)).thenReturn(Optional.of(estoque));
        when(estoquePessoalRepository.save(estoque)).thenReturn(estoque);
        when(modelMapper.map(estoque, EstoquePessoalDTO.class)).thenReturn(estoqueDTO);
        
        // When
        EstoquePessoalDTO result = estoquePessoalService.create(usuarioId, estoqueRequest);
        
        // Then
        assertNotNull(result);
        assertEquals(estoqueDTO, result);
        
        verify(usuarioRepository).findById(usuarioId);
        verify(medicamentoRepository).findByIdAndUsuarioId(medicamentoId, usuarioId);
        verify(estoquePessoalRepository).findByUsuarioAndMedicamento(usuario, medicamento);
        verify(estoquePessoalRepository).save(estoque);
        verify(modelMapper).map(estoque, EstoquePessoalDTO.class);
    }

    @Test
    void shouldUpdateInventoryItem() {
        // Given
        when(estoquePessoalRepository.findById(estoqueId)).thenReturn(Optional.of(estoque));
        when(estoquePessoalRepository.save(estoque)).thenReturn(estoque);
        when(modelMapper.map(estoque, EstoquePessoalDTO.class)).thenReturn(estoqueDTO);
        
        // When
        EstoquePessoalDTO result = estoquePessoalService.update(estoqueId, usuarioId, estoqueRequest);
        
        // Then
        assertNotNull(result);
        assertEquals(estoqueDTO, result);
        
        verify(estoquePessoalRepository).findById(estoqueId);
        verify(estoquePessoalRepository).save(estoque);
        verify(modelMapper).map(estoque, EstoquePessoalDTO.class);
    }

    @Test
    void shouldUpdateInventoryItemWithNewMedicamento() {
        // Given
        UUID newMedicamentoId = UUID.randomUUID();
        Medicamento newMedicamento = new Medicamento();
        newMedicamento.setId(newMedicamentoId);
        
        EstoquePessoalRequest requestWithNewMedicamento = new EstoquePessoalRequest();
        requestWithNewMedicamento.setMedicamentoId(newMedicamentoId);
        requestWithNewMedicamento.setQuantidadeAtual(15);
        requestWithNewMedicamento.setQuantidadeAlerta(3);
        
        when(estoquePessoalRepository.findById(estoqueId)).thenReturn(Optional.of(estoque));
        when(medicamentoRepository.findByIdAndUsuarioId(newMedicamentoId, usuarioId)).thenReturn(Optional.of(newMedicamento));
        when(estoquePessoalRepository.save(estoque)).thenReturn(estoque);
        when(modelMapper.map(estoque, EstoquePessoalDTO.class)).thenReturn(estoqueDTO);
        
        // When
        EstoquePessoalDTO result = estoquePessoalService.update(estoqueId, usuarioId, requestWithNewMedicamento);
        
        // Then
        assertNotNull(result);
        assertEquals(estoqueDTO, result);
        
        verify(estoquePessoalRepository).findById(estoqueId);
        verify(medicamentoRepository).findByIdAndUsuarioId(newMedicamentoId, usuarioId);
        verify(estoquePessoalRepository).save(estoque);
        verify(modelMapper).map(estoque, EstoquePessoalDTO.class);
    }

    @Test
    void shouldDeleteInventoryItem() {
        // Given
        when(estoquePessoalRepository.findById(estoqueId)).thenReturn(Optional.of(estoque));
        
        // When
        estoquePessoalService.delete(estoqueId, usuarioId);
        
        // Then
        verify(estoquePessoalRepository).findById(estoqueId);
        verify(estoquePessoalRepository).delete(estoque);
    }

    @Test
    void shouldGetLowStockInventoryItems() {
        // Given
        List<EstoquePessoal> estoques = Arrays.asList(estoque);
        
        when(estoquePessoalRepository.findEstoqueBaixo(usuarioId)).thenReturn(estoques);
        when(modelMapper.map(estoque, EstoquePessoalDTO.class)).thenReturn(estoqueDTO);
        
        // When
        List<EstoquePessoalDTO> result = estoquePessoalService.getEstoqueBaixo(usuarioId);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(estoqueDTO, result.get(0));
        
        verify(estoquePessoalRepository).findEstoqueBaixo(usuarioId);
        verify(modelMapper).map(estoque, EstoquePessoalDTO.class);
    }
}
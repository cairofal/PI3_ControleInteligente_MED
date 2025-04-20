package com.suscompanion.dto.receita;

import com.suscompanion.dto.medicamento.MedicamentoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DTO for returning prescription information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceitaDTO {

    private UUID id;
    private UUID usuarioId;
    private String medicoNome;
    private String medicoCrm;
    private LocalDate dataEmissao;
    private LocalDate dataValidade;
    private String observacoes;
    private String imagemUrl;
    private List<ReceitaItemDTO> itens = new ArrayList<>();
    private LocalDateTime criadoEm;
    private boolean valida;
}
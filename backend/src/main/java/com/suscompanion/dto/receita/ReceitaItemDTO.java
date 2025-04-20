package com.suscompanion.dto.receita;

import com.suscompanion.dto.medicamento.MedicamentoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for returning prescription item information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceitaItemDTO {

    private UUID id;
    private UUID receitaId;
    private MedicamentoDTO medicamento;
    private String descricao;
    private String posologia;
    private Integer quantidade;
    private LocalDateTime criadoEm;
}
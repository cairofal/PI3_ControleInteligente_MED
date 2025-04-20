package com.suscompanion.dto.estoque;

import com.suscompanion.dto.medicamento.MedicamentoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for returning personal inventory information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstoquePessoalDTO {

    private UUID id;
    private UUID usuarioId;
    private MedicamentoDTO medicamento;
    private Integer quantidadeAtual;
    private Integer quantidadeAlerta;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    private boolean estoqueBaixo;
}
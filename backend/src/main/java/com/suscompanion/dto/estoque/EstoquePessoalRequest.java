package com.suscompanion.dto.estoque;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for personal inventory creation and update requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstoquePessoalRequest {

    @NotNull(message = "ID do medicamento é obrigatório")
    private UUID medicamentoId;

    @NotNull(message = "Quantidade atual é obrigatória")
    @Min(value = 0, message = "Quantidade atual não pode ser negativa")
    private Integer quantidadeAtual;

    @Positive(message = "Quantidade de alerta deve ser um número positivo")
    private Integer quantidadeAlerta = 5;
}
package com.suscompanion.dto.receita;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for prescription item creation and update requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceitaItemRequest {

    private UUID medicamentoId;

    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;

    @NotBlank(message = "Posologia é obrigatória")
    private String posologia;

    @Positive(message = "Quantidade deve ser um número positivo")
    private Integer quantidade;
}
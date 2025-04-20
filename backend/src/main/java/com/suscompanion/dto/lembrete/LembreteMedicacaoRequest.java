package com.suscompanion.dto.lembrete;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for medication reminder creation and update requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LembreteMedicacaoRequest {

    @NotNull(message = "ID do medicamento é obrigatório")
    private UUID medicamentoId;

    @NotEmpty(message = "Horários são obrigatórios")
    private List<LocalTime> horarios;

    private List<Integer> diasSemana;

    @Positive(message = "Quantidade da dose deve ser um número positivo")
    private Double quantidadeDose;

    private String instrucoes;

    private Boolean ativo = true;
}
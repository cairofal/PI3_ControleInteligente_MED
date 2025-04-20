package com.suscompanion.dto.lembrete;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.suscompanion.dto.medicamento.MedicamentoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for returning medication reminder information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LembreteMedicacaoDTO {

    private UUID id;
    private UUID usuarioId;
    private MedicamentoDTO medicamento;
    private List<LocalTime> horarios;
    private List<Integer> diasSemana;
    private Double quantidadeDose;
    private String instrucoes;
    private Boolean ativo;
    private LocalDateTime criadoEm;
    private boolean paraHoje;
}
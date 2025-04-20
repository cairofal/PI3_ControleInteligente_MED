package com.suscompanion.dto.medicamento;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for returning medication information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicamentoDTO {

    private UUID id;
    private String nomeCompleto;
    private String nomeSimplificado;
    private String dosagem;
    private String tipo;
    private String fotoUrl;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
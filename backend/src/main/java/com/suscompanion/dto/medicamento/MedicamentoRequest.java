package com.suscompanion.dto.medicamento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for medication creation and update requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicamentoRequest {

    @NotBlank(message = "Nome completo é obrigatório")
    @Size(max = 100, message = "Nome completo deve ter no máximo 100 caracteres")
    private String nomeCompleto;

    @Size(max = 50, message = "Nome simplificado deve ter no máximo 50 caracteres")
    private String nomeSimplificado;

    @Size(max = 50, message = "Dosagem deve ter no máximo 50 caracteres")
    private String dosagem;

    @Size(max = 30, message = "Tipo deve ter no máximo 30 caracteres")
    private String tipo;

    private String fotoUrl;
}
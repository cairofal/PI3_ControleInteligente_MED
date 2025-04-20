package com.suscompanion.dto.saude;

import com.suscompanion.model.MonitoramentoSaude.TipoMonitoramento;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for health monitoring creation and update requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonitoramentoSaudeRequest {

    @NotNull(message = "Tipo de monitoramento é obrigatório")
    private TipoMonitoramento tipo;

    // Blood pressure fields
    @Min(value = 50, message = "Valor sistólica deve ser pelo menos 50")
    @Max(value = 300, message = "Valor sistólica deve ser no máximo 300")
    private Integer valorSistolica;

    @Min(value = 30, message = "Valor diastólica deve ser pelo menos 30")
    @Max(value = 200, message = "Valor diastólica deve ser no máximo 200")
    private Integer valorDiastolica;

    @Positive(message = "Pulsação deve ser um número positivo")
    @Max(value = 250, message = "Pulsação deve ser no máximo 250")
    private Integer pulsacao;

    // Blood glucose fields
    @Positive(message = "Valor de glicemia deve ser um número positivo")
    private BigDecimal valorGlicemia;

    private Boolean jejum;

    private String observacoes;

    @PastOrPresent(message = "Data de registro deve ser no passado ou presente")
    private LocalDateTime dataRegistro = LocalDateTime.now();
}

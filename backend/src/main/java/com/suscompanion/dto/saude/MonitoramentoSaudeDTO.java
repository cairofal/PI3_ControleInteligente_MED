package com.suscompanion.dto.saude;

import com.suscompanion.model.MonitoramentoSaude.TipoMonitoramento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for returning health monitoring information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonitoramentoSaudeDTO {

    private UUID id;
    private UUID usuarioId;
    private TipoMonitoramento tipo;

    // Blood pressure fields
    private Integer valorSistolica;
    private Integer valorDiastolica;
    private Integer pulsacao;

    // Blood glucose fields
    private BigDecimal valorGlicemia;
    private Boolean jejum;

    private String observacoes;
    private LocalDateTime dataRegistro;
    private LocalDateTime criadoEm;
}

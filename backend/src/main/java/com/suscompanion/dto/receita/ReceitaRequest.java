package com.suscompanion.dto.receita;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DTO for prescription creation and update requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceitaRequest {

    @Size(max = 100, message = "Nome do médico deve ter no máximo 100 caracteres")
    private String medicoNome;

    @Size(max = 20, message = "CRM do médico deve ter no máximo 20 caracteres")
    private String medicoCrm;

    @NotNull(message = "Data de emissão é obrigatória")
    @PastOrPresent(message = "Data de emissão deve ser no passado ou presente")
    private LocalDate dataEmissao;

    private LocalDate dataValidade;

    private String observacoes;

    private String imagemUrl;

    @Valid
    private List<ReceitaItemRequest> itens = new ArrayList<>();
}
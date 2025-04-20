package com.suscompanion.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a medical prescription.
 */
@Entity
@Table(name = "receitas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Receita {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Size(max = 100, message = "Nome do médico deve ter no máximo 100 caracteres")
    @Column(name = "medico_nome", length = 100)
    private String medicoNome;

    @Size(max = 20, message = "CRM do médico deve ter no máximo 20 caracteres")
    @Column(name = "medico_crm", length = 20)
    private String medicoCrm;

    @NotNull(message = "Data de emissão é obrigatória")
    @PastOrPresent(message = "Data de emissão deve ser no passado ou presente")
    @Column(name = "data_emissao", nullable = false)
    private LocalDate dataEmissao;

    @FutureOrPresent(message = "Data de validade deve ser no presente ou futuro")
    @Column(name = "data_validade")
    private LocalDate dataValidade;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @Pattern(regexp = "^(https?|ftp)://.*$", message = "URL da imagem deve ser válida")
    @Column(name = "imagem_url")
    private String imagemUrl;

    @OneToMany(mappedBy = "receita", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReceitaItem> itens = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    /**
     * Check if the prescription is still valid.
     * @return true if the prescription is valid (not expired)
     */
    @Transient
    public boolean isValida() {
        if (dataValidade == null) {
            return true; // No expiration date means it's always valid
        }
        return !LocalDate.now().isAfter(dataValidade);
    }

    /**
     * Add an item to the prescription.
     * @param item the item to add
     * @return this prescription
     */
    public Receita addItem(ReceitaItem item) {
        itens.add(item);
        item.setReceita(this);
        return this;
    }

    /**
     * Remove an item from the prescription.
     * @param item the item to remove
     * @return this prescription
     */
    public Receita removeItem(ReceitaItem item) {
        itens.remove(item);
        item.setReceita(null);
        return this;
    }
}

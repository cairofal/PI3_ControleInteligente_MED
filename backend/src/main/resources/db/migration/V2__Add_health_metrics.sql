-- Tabela de monitoramento de saúde
CREATE TABLE IF NOT EXISTS monitoramento_saude (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    usuario_id UUID NOT NULL REFERENCES usuarios(id),
    tipo VARCHAR(20) NOT NULL, -- 'PRESSAO', 'GLICEMIA'
    valor_sistolica INTEGER, -- para pressão
    valor_diastolica INTEGER, -- para pressão
    valor_glicemia DECIMAL(5,2), -- para glicemia
    jejum BOOLEAN, -- para glicemia
    pulsacao INTEGER, -- para pressão
    observacoes TEXT,
    data_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para melhorar performance
CREATE INDEX IF NOT EXISTS
 idx_monitoramento_usuario ON monitoramento_saude(usuario_id);
CREATE INDEX IF NOT EXISTS
 idx_monitoramento_tipo ON monitoramento_saude(tipo);
CREATE INDEX IF NOT EXISTS
idx_monitoramento_data ON monitoramento_saude(data_registro);
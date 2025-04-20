-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Tabela de usuários
CREATE TABLE usuarios (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    cpf VARCHAR(11) UNIQUE,
    data_nascimento DATE,
    senha VARCHAR(100) NOT NULL,
    telefone VARCHAR(20),
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de refresh tokens
CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    usuario_id UUID NOT NULL REFERENCES usuarios(id),
    token VARCHAR(255) NOT NULL,
    expiracao TIMESTAMP NOT NULL,
    revogado BOOLEAN DEFAULT FALSE,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de medicamentos
CREATE TABLE medicamentos (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nome_completo VARCHAR(100) NOT NULL,
    nome_simplificado VARCHAR(50),
    dosagem VARCHAR(50),
    tipo VARCHAR(30),
    foto_url VARCHAR(255),
    usuario_id UUID NOT NULL REFERENCES usuarios(id),
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de receitas médicas
CREATE TABLE receitas (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    usuario_id UUID NOT NULL REFERENCES usuarios(id),
    medico_nome VARCHAR(100),
    medico_crm VARCHAR(20),
    data_emissao DATE NOT NULL,
    data_validade DATE,
    observacoes TEXT,
    imagem_url VARCHAR(255),
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de itens da receita
CREATE TABLE receita_itens (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    receita_id UUID NOT NULL REFERENCES receitas(id),
    medicamento_id UUID REFERENCES medicamentos(id),
    descricao TEXT NOT NULL,
    posologia TEXT NOT NULL,
    quantidade INTEGER,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de lembretes de medicação
CREATE TABLE lembretes_medicacao (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    usuario_id UUID NOT NULL REFERENCES usuarios(id),
    medicamento_id UUID NOT NULL REFERENCES medicamentos(id),
    horarios TIME[] NOT NULL,
    dias_semana INTEGER[], -- 0-6 (Domingo-Sábado)
    quantidade_dose DECIMAL(10,2),
    instrucoes TEXT,
    ativo BOOLEAN DEFAULT TRUE,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de estoque pessoal
CREATE TABLE estoque_pessoal (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    usuario_id UUID NOT NULL REFERENCES usuarios(id),
    medicamento_id UUID NOT NULL REFERENCES medicamentos(id),
    quantidade_atual INTEGER NOT NULL,
    quantidade_alerta INTEGER DEFAULT 5,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para melhorar performance
CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_medicamentos_usuario ON medicamentos(usuario_id);
CREATE INDEX idx_receitas_usuario ON receitas(usuario_id);
CREATE INDEX idx_receita_itens_receita ON receita_itens(receita_id);
CREATE INDEX idx_lembretes_usuario ON lembretes_medicacao(usuario_id);
CREATE INDEX idx_lembretes_medicamento ON lembretes_medicacao(medicamento_id);
CREATE INDEX idx_estoque_usuario ON estoque_pessoal(usuario_id);
CREATE INDEX idx_estoque_medicamento ON estoque_pessoal(medicamento_id);
CREATE INDEX idx_refresh_tokens_usuario ON refresh_tokens(usuario_id);
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
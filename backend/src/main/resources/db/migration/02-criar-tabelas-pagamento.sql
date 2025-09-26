-- ================================
-- TABELA TIPO DE REQUISITÓRIO
-- ================================
CREATE TABLE requisitorio_tipo (
    id_tipo BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,  -- Ex: PRECAT, RPV, CESSAO, PENHORA, HONOR
    descricao VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE requisitorio_tipo IS 'Tipos de requisitórios: precatório, RPV, cessão de crédito, penhora, honorários.';
COMMENT ON COLUMN requisitorio_tipo.codigo IS 'Código curto do tipo de requisitório.';


-- ================================
-- TABELA PRINCIPAL DE REQUISITÓRIOS
-- ================================
CREATE TABLE requisitorio_pagamento (
    id_requisitorio BIGSERIAL PRIMARY KEY,
    numero_processo VARCHAR(30) NOT NULL,
    id_tipo BIGINT NOT NULL REFERENCES requisitorio_tipo(id_tipo),
    valor_principal NUMERIC(18,2) NOT NULL,
    valor_atualizado NUMERIC(18,2),
    data_requisicao DATE NOT NULL,
    data_pagamento DATE,
    situacao VARCHAR(50) NOT NULL, -- Ex: EM ANDAMENTO, PAGO, CANCELADO
    cpf_cnpj_credor VARCHAR(20) NOT NULL,
    nome_credor VARCHAR(200) NOT NULL,
    tribunal_origem VARCHAR(100), -- TJ, TRF etc.
    unidade_responsavel VARCHAR(100), -- Vara, Juizado
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE requisitorio_pagamento IS 'Tabela principal de requisitórios de pagamento (Precatórios, RPVs, etc.).';


-- ================================
-- CESSÃO DE CRÉDITO (OPCIONAL)
-- ================================
CREATE TABLE requisitorio_cessao_credito (
    id_cessao BIGSERIAL PRIMARY KEY,
    id_requisitorio BIGINT NOT NULL REFERENCES requisitorio_pagamento(id_requisitorio) ON DELETE CASCADE,
    cessionario_nome VARCHAR(200) NOT NULL,
    cessionario_cpf_cnpj VARCHAR(20) NOT NULL,
    data_cessao DATE NOT NULL,
    valor_cessao NUMERIC(18,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE requisitorio_cessao_credito IS 'Tabela para registro de cessões de crédito vinculadas a um requisitório.';


-- ================================
-- PENHORA NO ROSTO DOS AUTOS
-- ================================
CREATE TABLE requisitorio_penhora (
    id_penhora BIGSERIAL PRIMARY KEY,
    id_requisitorio BIGINT NOT NULL REFERENCES requisitorio_pagamento(id_requisitorio) ON DELETE CASCADE,
    orgao_beneficiario VARCHAR(200) NOT NULL,
    processo_penhora VARCHAR(30),
    valor_penhorado NUMERIC(18,2),
    data_penhora DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE requisitorio_penhora IS 'Registro de penhoras no rosto dos autos sobre um requisitório.';


-- ================================
-- HONORÁRIOS ADVOCATÍCIOS
-- ================================
CREATE TABLE requisitorio_honorario (
    id_honorario BIGSERIAL PRIMARY KEY,
    id_requisitorio BIGINT NOT NULL REFERENCES requisitorio_pagamento(id_requisitorio) ON DELETE CASCADE,
    advogado_nome VARCHAR(200) NOT NULL,
    advogado_oab VARCHAR(50),
    percentual NUMERIC(5,2),       -- Percentual sobre valor principal
    valor_honorario NUMERIC(18,2), -- Valor fixo ou calculado
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE requisitorio_honorario IS 'Honorários advocatícios vinculados ao requisitório.';


-- ================================
-- PARCELAS / PAGAMENTOS (OPCIONAL)
-- ================================
CREATE TABLE requisitorio_parcela (
    id_parcela BIGSERIAL PRIMARY KEY,
    id_requisitorio BIGINT NOT NULL REFERENCES requisitorio_pagamento(id_requisitorio) ON DELETE CASCADE,
    numero_parcela INT NOT NULL,
    valor_parcela NUMERIC(18,2) NOT NULL,
    data_vencimento DATE NOT NULL,
    data_pagamento DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE requisitorio_parcela IS 'Parcelas de pagamento do requisitório, se houver parcelamento.';


-- ================================
-- PERMISSÕES
-- ================================
GRANT ALL ON ALL TABLES IN SCHEMA public TO web;
GRANT ALL ON ALL SEQUENCES IN SCHEMA public TO web;
GRANT ALL ON ALL TABLES IN SCHEMA public TO postgres;
GRANT ALL ON ALL SEQUENCES IN SCHEMA public TO postgres;

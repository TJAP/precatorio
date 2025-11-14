-- Criação da tabela tipo_indice_bacen
CREATE TABLE tipo_indice_bacen (
                                   id SERIAL PRIMARY KEY,                -- Número com autoincremento
                                   descricao VARCHAR(255) NOT NULL       -- Descrição do tipo de índice
);

-- Criação da tabela indice_bacen
CREATE TABLE indice_bacen (
                              id SERIAL PRIMARY KEY,                -- Número com autoincremento
                              id_tipo_indice_bacen INT NOT NULL,    -- FK para tipo_indice_bacen
                              dt_referencia DATE NOT NULL,          -- Data de referência
                              valor NUMERIC(20,6) NOT NULL,         -- Valor com casas decimais

                              CONSTRAINT fk_indice_bacen_tipo
                                  FOREIGN KEY (id_tipo_indice_bacen)
                                      REFERENCES tipo_indice_bacen (id)
                                      ON DELETE RESTRICT
                                      ON UPDATE CASCADE
);

drop table tabela_irrf

-- Criação da tabela tabela_irrf
CREATE TABLE tabela_irrf (
                             id SERIAL PRIMARY KEY,               -- Número autoincremento
                             exercicio numeric not null,
                             dt_periodo_inicio DATE not null,
                             dt_periodo_fim DATE not null,
                             faixa_inicial NUMERIC(15,2) NOT NULL, -- Valor inicial da faixa (moeda)
                             faixa_final NUMERIC(15,2) NOT NULL,   -- Valor final da faixa (moeda)
                             aliquota NUMERIC(4,2) NOT NULL,       -- Percentual da alíquota (ex.: 0, 7.5, 15, 22.5, 27.5)
                             deducao NUMERIC(15,2) NOT NULL        -- Valor de dedução (moeda)
);

-- Índice para busca por dt_referencia (opcional, mas recomendável se for usar muito em consultas)
CREATE INDEX idx_indice_bacen_dt_ref
    ON indice_bacen (dt_referencia);

-- Índice para busca por tipo de índice (opcional)
CREATE INDEX idx_indice_bacen_tipo
    ON indice_bacen (id_tipo_indice_bacen);

-- Índice opcional para consultas por faixa (recomendável)
CREATE INDEX idx_tabela_irrf_faixa
    ON tabela_irrf (faixa_inicial, faixa_final);

GRANT ALL ON TABLE tipo_indice_bacen TO postgres;
GRANT ALL ON TABLE tipo_indice_bacen TO web;
GRANT ALL ON TABLE tipo_indice_bacen TO u44181;

GRANT ALL ON TABLE indice_bacen TO postgres;
GRANT ALL ON TABLE indice_bacen TO web;
GRANT ALL ON TABLE indice_bacen TO u44181;

GRANT ALL ON TABLE tabela_irrf TO postgres;
GRANT ALL ON TABLE tabela_irrf TO web;
GRANT ALL ON TABLE tabela_irrf TO u44181;


-- Tabela: tipo_previdencia
-- =========================================================

CREATE TABLE tipo_previdencia (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    categoria VARCHAR(50) NOT NULL,
    aliquota_contribuicao NUMERIC(5,2),
    aliquota_chefia NUMERIC(5,2),
    possui_teto BOOLEAN DEFAULT FALSE,
    ativo BOOLEAN DEFAULT TRUE,
    lei_referencia VARCHAR(50),
    data_criacao TIMESTAMP DEFAULT now(),
    data_atualizacao TIMESTAMP DEFAULT now()
);

-- Comentários nas colunas
COMMENT ON COLUMN tipo_previdencia.id IS 'Identificador único';
COMMENT ON COLUMN tipo_previdencia.codigo IS 'Código curto ou sigla (ex: RPPS, RPPM, RGPS)';
COMMENT ON COLUMN tipo_previdencia.nome IS 'Nome do regime (ex: "RPPS Estadual", "RPPM Militar")';
COMMENT ON COLUMN tipo_previdencia.descricao IS 'Descrição detalhada, regras e observações';
COMMENT ON COLUMN tipo_previdencia.categoria IS 'Categoria de servidor: CIVIL, MILITAR, FEDERAL, COMISSIONADO';
COMMENT ON COLUMN tipo_previdencia.aliquota_contribuicao IS 'Alíquota de contribuição (%)';
COMMENT ON COLUMN tipo_previdencia.aliquota_chefia IS 'Se houver contribuição patronal (%)';
COMMENT ON COLUMN tipo_previdencia.possui_teto IS 'Indica se há limite máximo de contribuição/benefício';
COMMENT ON COLUMN tipo_previdencia.ativo IS 'Se o regime está ativo';
COMMENT ON COLUMN tipo_previdencia.lei_referencia IS 'Lei ou decreto que regulamenta';
COMMENT ON COLUMN tipo_previdencia.data_criacao IS 'Data de criação do registro';
COMMENT ON COLUMN tipo_previdencia.data_atualizacao IS 'Data de atualização do registro';

-- Grants
GRANT ALL PRIVILEGES ON TABLE tipo_previdencia TO web;
GRANT ALL PRIVILEGES ON TABLE tipo_previdencia TO postgres;

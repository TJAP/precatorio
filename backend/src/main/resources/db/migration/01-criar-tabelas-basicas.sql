-- =========================================================
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

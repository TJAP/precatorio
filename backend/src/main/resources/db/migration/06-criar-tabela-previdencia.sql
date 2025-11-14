select id, situcao_funcional_credor, * from precatorio.precatorio p where p.id = 25087

select count(*), situcao_funcional_credor from precatorio.precatorio p group by situcao_funcional_credor

select * from precatorio.processo_deducao order by id_tipo_deducao, id desc

select * from precatorio.processo_deducao where numero_processo_destino = '00628349020148030001'

select * from precatorio.tipo_deducao td

select * from precatorio.previdencia tap

select distinct codigo from precatorio.tipo_tributacao_precatorio ttp

select * from precatorio.tipo_tributacao_precatorio ttp

commit
select id, r.situcao_funcional_credor, * from precatorio.precatorio r

select distinct situcao_funcional_credor from precatorio.precatorio r

update precatorio.precatorio set situcao_funcional_credor = 1 where situcao_funcional_credor = 'Efetivo'

update precatorio.precatorio set situcao_funcional_credor = 2 where situcao_funcional_credor = 'Sem vínculo'

update precatorio.precatorio set situcao_funcional_credor = 3 where situcao_funcional_credor = 'Contrato/Função'

update precatorio.precatorio set situcao_funcional_credor = 4 where situcao_funcional_credor = 'Aposentado'

update precatorio.precatorio set situcao_funcional_credor = 3 where situcao_funcional_credor isnull

update precatorio.precatorio set situcao_funcional_credor = 3 where situcao_funcional_credor isnull

update precatorio.precatorio set situcao_funcional_credor = 3 where situcao_funcional_credor = ''

select * from precatorio.tipo_credor_precatorio tcp

select * from processo_precatorio pp where pp.numero_proc_origem = '0005517-59.2022.8.03.0000'

select id_precatorio_tucujuris, * from precatorio.precatorio p where p.num_processo_tucujuris = '0005517-59.2022.8.03.0000'

select id, situacao, doc_credor, tp_precatorio, * from precatorio.precatorio p where p.id_processo = '00055175920228030000'

select id, id_precatorio_tucujuris, * from precatorio.precatorio p where p.num_processo_tucujuris = '0003882-48.2019.8.03.0000'

select * from public.processo_precatorio_item where fk_processo_precatorio = 26924

select * FROM processo_precatorio_valor where fk_processo_precatorio = 26924

select * from lotacao where id = 1001003

select * from precatorio.tipo_acao_precatorio tap

select id, id_tipo_credor, codigo, aplica_ir, aliquota_padrao, usa_tabela_progressiva, com_previdencia, com_rra
from precatorio.tipo_tributacao_precatorio order by 2, 3

select * from pessoas p where p.cpfcnpj = '01580464203'

00325820220178030001

GRANT ALL PRIVILEGES ON TABLE precatorio.tipo_tributacao_precatorio TO web;

GRANT ALL PRIVILEGES ON SEQUENCE precatorio.tipo_tributacao_precatorio_id_seq TO web;


select tt1_0.id_tipo_credor,tt1_0.id,tt1_0.aliquota_padrao,tt1_0.aplica_ir,tt1_0.ativo,tt1_0.codigo,tt1_0.com_previdencia,tt1_0.com_rra,tt1_0.descricao,tt1_0.observacao,tt1_0.origem_isencao,tt1_0.usa_tabela_progressiva from precatorio.tipo_tributacao_precatorio tt1_0
where tt1_0.id_tipo_credor=1

select (259685.21 - 43280.87)

select * from public.processo_precatorio pp where pp.id = 1250000348

select count(*), fk_processo_precatorio from public.processo_precatorio_item group by fk_processo_precatorio

select * from precatorio.precatorio p where p.id_precatorio_tucujuris = 26924

select * from public.processo_precatorio_prioridade ppp

select * from autos a where a.numero_cnj = '00038824820198030000'

select * from processo_precatorio pp where pp.fk_processo_2g = 1475039

select * from precatorio.indice_bacen ib


SELECT
    pid,
    usename,
    application_name,
    client_addr,
    state,
    wait_event_type,
    wait_event,
    now() - query_start AS tempo_execucao,
    query
FROM pg_stat_activity
WHERE datname = current_database()
and usename = 'adm_precatorio'

SELECT pg_terminate_backend(11220);
SELECT pg_terminate_backend(11221);
SELECT pg_terminate_backend(11222);
SELECT pg_terminate_backend(11223);

--ORDER BY tempo_execucao DESC







-- Sequence
CREATE SEQUENCE precatorio.tipo_credor_precatorio_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Tabela principal
CREATE TABLE precatorio.tipo_credor_precatorio (
    id BIGINT PRIMARY KEY DEFAULT nextval('precatorio.tipo_credor_precatorio_id_seq'),
    descricao VARCHAR(200) NOT NULL,                        -- Ex: "Honorários Sucumbenciais"
    tipo_pessoa VARCHAR(5),
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

-- Permissões
GRANT ALL PRIVILEGES ON TABLE precatorio.tipo_credor_precatorio TO web;
GRANT ALL PRIVILEGES ON SEQUENCE precatorio.tipo_credor_precatorio_id_seq TO web;

-- Inserts iniciais com base na imagem enviada
-- Ajustando as naturezas com o tipo de tributação mais provável:

INSERT INTO precatorio.tipo_credor_precatorio (descricao,  tipo_pessoa)
SELECT descricao, tipo_pessoa
FROM (
    VALUES
        ('Servidor Público Concursado', 'PF'),
        ('Servidor Público Não Concursado', 'PF'),
        ('PF - Sem Vinculo Com a Administração', 'PF'),
        ('PF - Verbas Indenizatórias', 'PF'),
        ('PJ - Verbas Indenizatórias', 'PJ'),
        ('PJ - Cessão de Mão de Obra', 'PJ'),
        ('PJ - Lucros Cessantes', 'PJ'),
        ('PJ - Serviços', 'PJ'),
        ('PJ - Outros', 'PJ'),
        ('PJ - Simples Nacional', 'PJ')
) AS t(descricao, tipo_pessoa);



--drop table previdencia;
--drop sequence seq_previdencia;

-- Criação da tabela PREVIDENCIA
CREATE TABLE precatorio.previdencia (
    id     BIGSERIAL PRIMARY KEY,
    nome               VARCHAR(100)       NOT NULL,
    tipo               VARCHAR(20)        NOT NULL,  -- Ex: RGPS, RPPS, etc.
    valor_teto         NUMERIC(15,2),
    banco              VARCHAR(50),
    agencia            VARCHAR(20),
    conta              VARCHAR(20),
    tipo_conta         VARCHAR(20),
    digito_conta       VARCHAR(5),
    ativo              CHAR(1) DEFAULT 'S' CHECK (ativo IN ('S', 'N')),
    dt_atualizacao     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Sequence criada automaticamente por causa do BIGSERIAL,
-- mas se quiser nomear explicitamente:
CREATE SEQUENCE precatorio.seq_previdencia
    START WITH 1
    INCREMENT BY 1
    OWNED BY precatorio.previdencia.id;

-- Ajustar a coluna para usar a sequence explicitamente (opcional)
ALTER TABLE precatorio.previdencia ALTER COLUMN id
SET DEFAULT nextval('precatorio.seq_previdencia');

-- Grant para o usuário WEB
GRANT SELECT, INSERT, UPDATE, DELETE ON precatorio.previdencia TO web;
GRANT USAGE, SELECT ON SEQUENCE precatorio.seq_previdencia TO web;

-- Comentários opcionais
COMMENT ON TABLE precatorio.previdencia IS 'Tabela de informações de previdência (RGPS, RPPS, etc.)';
COMMENT ON COLUMN precatorio.previdencia.nome IS 'Nome da previdência ou regime';
COMMENT ON COLUMN precatorio.previdencia.tipo IS 'Tipo da previdência (RGPS, RPPS, etc.)';
COMMENT ON COLUMN precatorio.previdencia.valor_teto IS 'Valor do teto de contribuição';
COMMENT ON COLUMN precatorio.previdencia.ativo IS 'Indica se a previdência está ativa (S/N)';
COMMENT ON COLUMN precatorio.previdencia.dt_atualizacao IS 'Data da última atualização do registro';


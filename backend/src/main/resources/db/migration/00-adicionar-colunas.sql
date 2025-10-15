ALTER TABLE precatorio.precatorio ADD id_ente_devedor int8 NULL;
ALTER TABLE precatorio.precatorio ADD dt_inicio_rra date NULL;
ALTER TABLE precatorio.precatorio ADD dt_fim_rra date NULL;
ALTER TABLE precatorio.precatorio ADD ano_vencimento int8 NULL;
ALTER TABLE precatorio.precatorio ADD vl_selic numeric(19,2) NULL;
ALTER TABLE precatorio.precatorio ADD dt_base_final_atualizacao date NULL;
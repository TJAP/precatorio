CREATE OR REPLACE VIEW precatorio.vw_acordo
	AS select
     rp.*,
     pes.nome,
     coalesce(rp.cpfcnpj, pes.cpfcnpj) as cpfcnpj_tratado,
     e.causidico_parte,
     r.desagio,
     a.numero_cnj
 from precatorio_acordo_processo_requerimento_pessoa rp
  inner join precatorio_acordo_processo_requerimento r
     on rp.fk_precatorio_acordo_processo_requerimento = r.id
 inner join autos a
     on r.fk_autos = a.id
 inner join processo_precatorio pp
     on a.id = pp.fk_processo_2g
 inner join precatorio_acordo_config pac
     on r.fk_precatorio_acordo_config = pac.id
 inner join pessoas pes
     on rp.fk_pessoas = pes.id
 inner join busca_envolvidos_processo(r.fk_autos, false) e
     on rp.fk_pessoas = e.fk_pessoas
     and ((rp.tipo_adesao = 'Honorários Contratuais') = e.causidico_parte)
 where pp.requerimento_acordo_direto
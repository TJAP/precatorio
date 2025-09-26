package br.jus.tjap.precatorio.modulos.requisitorio.repository;

import br.jus.tjap.precatorio.modulos.requisitorio.entity.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RequisitorioRepository extends JpaRepository<Requisitorio, Long> {
    @Query("select r from Requisitorio r")
        List<Requisitorio> findAllList();

        @Query("select p from Requisitorio p where p.codOrgaoJulgadorTucujuris = :id")
        List<Requisitorio> findAllListOrgaoJulgador(@Param("id") Integer id);

        /*
         * @Query(value =
         * "select p.id, id_processo, dt_cadastro, tp_precatorio, nome_credor, vl_global_requisicao, situacao, "
         * +
         * "dt_assinatura, doc_credor, doc_devedor, doc_adv_credor, id_orgao_julgador_pje, id_precatorio_tucujuris, "
         * +
         * "num_processo_tucujuris " +
         * "from precatorio.precatorio p " +
         * "left join processo_precatorio pp on p.id_processo = replace(replace(pp.numero_proc_origem,'.',''),'-','') "
         * +
         * "left join precatorio.processos_liberados_assinatura pal on p.id_processo = replace(replace(pal.numero_processo,'.',''),'-','') "
         * +
         * "and pp.fk_tipo_divida = p.tp_precatorio " +
         * "where dt_assinatura is null " +
         * "and id_orgao_julgador_tucujuris = :id " +
         * "and (case " +
         * "when (pp.id isnull and pal.numero_processo notnull) then true " +
         * "when (pp.id notnull and pal.numero_processo notnull) then true " +
         * "when (pp.id isnull and pal.numero_processo isnull) then true " +
         * "else false end) = true " +
         * "and situacao not in ('1','4','5','7','9','1') " +
         * "order by p.id desc", nativeQuery = true)
         */
        @Query(value = "select id, id_processo, dt_cadastro, tp_precatorio, nome_credor, vl_global_requisicao, situacao, "
                        +
                        "dt_assinatura, doc_credor, doc_devedor, doc_adv_credor, id_orgao_julgador_pje, id_precatorio_tucujuris,  "
                        +
                        "num_processo_tucujuris, msg_erro_distribuicao  " +
                        "from precatorio.precatorio p " +
                        "where 1 = 1  " +
                        "and id_processo not in ( " +
                        "	select replace(replace(pp.numero_proc_origem,'.',''),'-','') " +
                        "	from processo_precatorio pp " +
                        "	where pp.fk_tipo_divida = 1  " +
                        "	and pp.dt_finalizacao isnull " +
                        "	and p.id_processo = replace(replace(pp.numero_proc_origem,'.',''),'-','') " +
                        ") " +
                        "and p.dt_assinatura isnull " +
                        "and p.id_orgao_julgador_tucujuris = :id " +
                        "and p.num_processo_tucujuris isnull  " +
                        "and situacao not in ('1','4','5','7','9') " +
                        "order by p.id desc", nativeQuery = true)
        List<Object> findAllRequisitorioAssinaturaPorOrgao(@Param("id") Long id);

        @Query(value = "select id, id_processo, dt_cadastro, tp_precatorio, nome_credor, vl_global_requisicao, situacao, "
                        +
                        "dt_assinatura, doc_credor, doc_devedor, doc_adv_credor, id_orgao_julgador_pje, id_precatorio_tucujuris,"
                        +
                        "num_processo_tucujuris, msg_erro_distribuicao " +
                        "from precatorio.precatorio p " +
                        "where id_orgao_julgador_tucujuris = :id order by id desc", nativeQuery = true)
        List<Object> findAllRequisitorioPorOrgao(@Param("id") Long id);

        @Query(value = "select id, id_processo, dt_cadastro, tp_precatorio, nome_credor, vl_global_requisicao, situacao, "
                        +
                        "dt_assinatura, doc_credor, doc_devedor, doc_adv_credor, id_orgao_julgador_pje, id_precatorio_tucujuris,"
                        +
                        "num_processo_tucujuris " +
                        "from precatorio.precatorio p " +
                        "where id_orgao_julgador_tucujuris = :id order by id desc", nativeQuery = true)
        List<Object> findAllRequisitorio();

        @Query("select p from Requisitorio p where p.id = :id")
        Optional<Requisitorio> findRequisitorioById(@Param("id") Long id);

        // @Query("select p from Pagamento p where p.id = :id")
        // Optional<Pagamento> findPagamentoById(@Param("id") Long id);

        // @Query("select t from TipoArquivo t where t.retornaDoPje = true")
        // List<TipoArquivo> findTipoArquivoRetornaDoPje();

        // @Query("select t from TipoArquivo t where t.id = :id")
        // TipoArquivo getTipoArquivoPorId(@Param("id") Long id);

        // @Query("select a from Prioridade a where a.precatorio.id = :id order by a.id")
        // List<Prioridade> findPrioridadeByRequisitorio(@Param("id") Long id);

        @Query(value = "select count(id)+1 from precatorio.precatorio where tp_precatorio = :tipoPrecatorio", nativeQuery = true)
        Long getNextSeriesId(@Param("tipoPrecatorio") Integer tipoPrecatorio);

        @Query(value = "select id, id_processo, doc_credor from precatorio.precatorio p where p.id_processo = :id and p.situacao not in ('1','4','5','7')", nativeQuery = true)
        List<Object> findAllListIdProcessoPjeObj(@Param("id") String id);

        @Query(value = "SELECT valor FROM indicador_indice where fk_indicador_financeiro_tipo = :idTipoIndicadorFinanceiro order by id desc limit 1", nativeQuery = true)
        Long getUltimoIndicadorFinanceiroPorTipo(@Param("idTipoIndicadorFinanceiro") Integer idTipoIndicadorFinanceiro);

        @Query(value = "select fk_indicador_financeiro_tipo, valor FROM indicador_indice where fk_indicador_financeiro_tipo in (2,3) order by id desc limit 2", nativeQuery = true)
        List<Object> getUltimoSalarioEhMaiorValorInss();

        //@Query(value = "select * from precatorio.precatorio p where p.tp_precatorio = 1 and p.dt_assinatura between '2025-01-23 00:00:00.000' and '2025-01-29 00:00:00.000'", nativeQuery = true)
        @Query(value = "select * from precatorio.precatorio p where p.id= 16488", nativeQuery = true)
        List<Requisitorio> regerarDocumento();
}

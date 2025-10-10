package br.jus.tjap.precatorio.modulos.requisitorio.repository;

import br.jus.tjap.precatorio.modulos.requisitorio.entity.ProcessoDeducao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessoDeducaoRepository extends JpaRepository<ProcessoDeducao, Long> {

    @Query("select p from ProcessoDeducao p where p.numeroProcessoOrigem = :numeroProcessoOrigem order by p.id desc")
    List<ProcessoDeducao> findAllByProcessoOrigem(@Param("numeroProcessoOrigem") String numeroProcessoOrigem);
}

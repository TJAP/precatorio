package br.jus.tjap.precatorio.modulos.requisitorio.repository;

import br.jus.tjap.precatorio.modulos.requisitorio.entity.AcordoDireto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcordoDiretoRepository extends JpaRepository<AcordoDireto, Long> {

    @Query("select a from AcordoDireto a where a.numeroProcesso = :numeroProcessoOrigem order by a.id desc limit 2")
    List<AcordoDireto> findAllByNUmeroProcesso(@Param("numeroProcessoOrigem") String numeroProcessoOrigem);
}
package br.jus.tjap.precatorio.modulos.calculadora.repository;

import br.jus.tjap.precatorio.modulos.calculadora.entity.PrioridadeTucujuris;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrioridadeTucujurisRepository extends JpaRepository<PrioridadeTucujuris, Long> {

    @Query("select p from PrioridadeTucujuris p where p.idProcessoTucujuris = :idProcessoTucujuris")
    List<PrioridadeTucujuris> findAllByIdProcessoTucujuris(@Param("idProcessoTucujuris") Long idProcessoTucujuris);
}

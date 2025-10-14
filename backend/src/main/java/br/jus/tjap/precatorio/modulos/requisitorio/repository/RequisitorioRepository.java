package br.jus.tjap.precatorio.modulos.requisitorio.repository;

import br.jus.tjap.precatorio.modulos.requisitorio.entity.Requisitorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RequisitorioRepository extends JpaRepository<Requisitorio, Long> {

    @Query("select r from Requisitorio r where r.numProcessoTucujuris = :numeroProcesso")
    Optional<Requisitorio> buscarPorNumeroProcesso(@Param("numeroProcesso") String numeroProcesso);
}

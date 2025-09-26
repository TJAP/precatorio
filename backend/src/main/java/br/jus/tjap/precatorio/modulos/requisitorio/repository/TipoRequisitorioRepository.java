package br.jus.tjap.precatorio.modulos.requisitorio.repository;

import br.jus.tjap.precatorio.modulos.requisitorio.entity.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TipoRequisitorioRepository extends JpaRepository<TipoRequisitorio, Long>{
    
    @Query(value = "select t from TipoRequisitorio t")
    Optional<TipoRequisitorio> findAllList();


}

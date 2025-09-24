package br.jus.tjap.precatorio.modulos.requisitorio.repository;

import br.jus.tjap.precatorio.modulos.requisitorio.entity.Requisitorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequisitorioRepository extends JpaRepository<Requisitorio, Long> {
}

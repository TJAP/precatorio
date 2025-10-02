package br.jus.tjap.precatorio.modulos.tabelasbasicas.repository;


import br.jus.tjap.precatorio.modulos.tabelasbasicas.entity.EnteDevedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnteDevedorRepository  extends JpaRepository<EnteDevedor, Long> {
}
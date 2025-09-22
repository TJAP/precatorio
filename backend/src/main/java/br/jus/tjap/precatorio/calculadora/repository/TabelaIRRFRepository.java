package br.jus.tjap.precatorio.calculadora.repository;

import br.jus.tjap.precatorio.calculadora.entity.TabelaIRRF;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TabelaIRRFRepository extends JpaRepository<TabelaIRRF, Long> {

}

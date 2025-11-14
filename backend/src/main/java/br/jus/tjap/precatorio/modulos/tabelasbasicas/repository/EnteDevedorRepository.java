package br.jus.tjap.precatorio.modulos.tabelasbasicas.repository;


import br.jus.tjap.precatorio.modulos.tabelasbasicas.entity.EnteDevedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EnteDevedorRepository  extends JpaRepository<EnteDevedor, Long> {

    @Query("select e from EnteDevedor e where e.cnpj = :numeroCNPJ")
    EnteDevedor findByCnpj(@Param("numeroCNPJ") String numeroCNPJ);
}
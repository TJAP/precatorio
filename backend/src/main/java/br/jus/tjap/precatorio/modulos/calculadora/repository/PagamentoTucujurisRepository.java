package br.jus.tjap.precatorio.modulos.calculadora.repository;

import br.jus.tjap.precatorio.modulos.calculadora.entity.PagamentoTucujuris;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagamentoTucujurisRepository extends JpaRepository<PagamentoTucujuris, Long> {

    @Query("select p from PagamentoTucujuris p where p.idProcessoTucujuris = :idProcessoTucujuris order by id desc")
    List<PagamentoTucujuris> findAllByIdPagamentoTucujuris(@Param("idProcessoTucujuris") Long idProcessoTucujuris);

    @Query(value = "select codigo, descricao from bancos b where b.codigo = :idBanco", nativeQuery = true)
    Object getBanco(@Param("idBanco") Long idBanco);
}

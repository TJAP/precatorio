package br.jus.tjap.precatorio.modulos.calculadora.repository;

import br.jus.tjap.precatorio.modulos.calculadora.entity.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    @Query("select p from Pagamento p where p.idPrecatorioTucujuris = :idPrecatorioTucujuris AND p.valorPagamento IS NOT NULL")
    Optional<List<Pagamento>> findPagamentoPorIdPrecatorioTucujuris(@Param("idPrecatorioTucujuris") Long idPrecatorioTucujuris);
}

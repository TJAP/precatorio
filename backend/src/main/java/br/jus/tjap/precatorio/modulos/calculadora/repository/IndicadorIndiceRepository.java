package br.jus.tjap.precatorio.modulos.calculadora.repository;

import br.jus.tjap.precatorio.modulos.calculadora.entity.IndiceBacen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IndicadorIndiceRepository extends JpaRepository<IndiceBacen, Long> {

    @Query("SELECT i FROM IndiceBacen i " +
            "WHERE i.tipoInidicadorIndice.id = :tipoIndice " +
            "AND i.dataInicioVigencia BETWEEN :startDate AND :endDate " +
            "ORDER BY i.dataInicioVigencia ASC")
    List<IndiceBacen> findByTipoIndiceAndPeriodo(Long tipoIndice, LocalDate startDate, LocalDate endDate);
}

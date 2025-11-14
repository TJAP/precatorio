package br.jus.tjap.precatorio.modulos.requisitorio.dto;

import br.jus.tjap.precatorio.modulos.requisitorio.entity.Requisitorio;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrioridadeDTO {

    private Long id;
    private Integer idTipoPrioridade;
    private LocalDate dataLancamento;
    private Integer comarca;
    private RequisitorioDTO requisitorioDTO;
}

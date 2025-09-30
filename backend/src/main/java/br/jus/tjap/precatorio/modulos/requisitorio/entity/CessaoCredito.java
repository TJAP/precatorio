package br.jus.tjap.precatorio.modulos.requisitorio.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import br.jus.tjap.precatorio.modulos.requisitorio.dto.*;
import br.jus.tjap.precatorio.util.DateUtil;
import br.jus.tjap.precatorio.util.StringUtil;
import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.*;

@Entity
@Table(name = Requisitorio.TABLE_NAME, schema="precatorio")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class CessaoCredito {
    public static final String TABLE_NAME = "precatorio";

    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen_" + TABLE_NAME)
    @SequenceGenerator(name = "gen_" + TABLE_NAME, sequenceName = "precatorio.sq_" + TABLE_NAME, allocationSize = 1)
    
    @Column(name = "in_sessao_credito")
    private boolean sessaoCredito;
    @Column(name = "vl_sessao_credito")
    private BigDecimal vlSessaoCredito;
    @Column(name = "nm_meses_rendimento_recebido_acumulado")
    private Integer nmMesesRendimentoRecebidoAcumulado;
}

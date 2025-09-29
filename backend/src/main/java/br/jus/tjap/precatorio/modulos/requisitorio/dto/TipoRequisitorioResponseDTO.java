package br.jus.tjap.precatorio.modulos.requisitorio.dto;

import br.jus.tjap.precatorio.util.StringUtil;
import jakarta.persistence.Column;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TipoRequisitorioResponseDTO {
    private Integer tipoPrecatorio;
    private String dsTipoObrigacao;
    private String nomeBancoCredor;
    private String agenciaCredor;
    private String contaCorrenteCredor;
    private String agenciaAdvCredor;
    private String contaCorrenteAdvCredor;
    private String situacaoFuncionalCredor;
    private Integer percentHonorAdvCredor;
    private String tpTributacaoAdvCredor;
    private String nomeBancoAdvCredor;
    private Boolean sessaoCredito;
    private BigDecimal vlSessaoCredito;
}

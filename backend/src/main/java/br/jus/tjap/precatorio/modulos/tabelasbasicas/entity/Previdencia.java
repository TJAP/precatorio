package br.jus.tjap.precatorio.modulos.tabelasbasicas.entity;

import br.jus.tjap.precatorio.modulos.tabelasbasicas.dto.PrevidenciaDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(schema = "precatorio", name="previdencia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Previdencia implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_previdencia")
    @SequenceGenerator(name = "seq_previdencia", sequenceName = "seq_previdencia", allocationSize = 1)
    private Long id;

    private String nome; // PF ou PJ
    private String tipo;
    private BigDecimal valorTeto;
    private String banco;
    private String agencia;
    private String conta;
    private String tipoConta;
    private String digitoConta;
    private LocalDateTime dtAtualizacao;


    public PrevidenciaDTO toMetadado(){
        var dto = new PrevidenciaDTO();

        dto.setId(this.id);
        dto.setNome(this.nome);
        dto.setTipo(this.tipo);
        dto.setValorTeto(this.valorTeto);
        dto.setBanco(this.banco);
        dto.setAgencia(this.agencia);
        dto.setConta(this.conta);
        dto.setTipoConta(this.tipoConta);
        dto.setDigitoConta(this.digitoConta);
        dto.setDtAtualizacao(this.dtAtualizacao);

        return dto;
    }

}

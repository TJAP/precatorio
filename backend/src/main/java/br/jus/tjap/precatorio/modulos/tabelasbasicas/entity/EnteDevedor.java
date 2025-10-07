package br.jus.tjap.precatorio.modulos.tabelasbasicas.entity;

import br.jus.tjap.precatorio.modulos.tabelasbasicas.dto.EnteDevedorDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(schema = "precatorio", name="ente_devedor")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnteDevedor implements Serializable {

    @Id
    private Long id;

    @Column(name = "ente")
    private String nome;

    @Column(name = "cnpj")
    private String cnpj;

    @Column(name = "limite_prioridade")
    private BigDecimal limitePrioridade;

    @Column(name = "com_vinculo")
    private String comVinculo;

    @Column(name = "sem_vinculo")
    private String semVinculo;

    @Column(name = "conta")
    private String numeroConta;

    @Column(name = "acordo")
    private String numeroContaAcordo;

    public EnteDevedorDTO toMetadado(){
        EnteDevedorDTO dto = new EnteDevedorDTO();

        dto.setId(this.id);
        dto.setCnpj(this.cnpj);
        dto.setNome(this.nome);
        dto.setComVinculo(this.comVinculo);
        dto.setSemVinculo(this.semVinculo);
        dto.setNumeroConta(this.numeroConta);
        dto.setLimitePrioridade(this.limitePrioridade);
        dto.setNumeroContaAcordo(this.numeroContaAcordo);

        return dto;
    }
}

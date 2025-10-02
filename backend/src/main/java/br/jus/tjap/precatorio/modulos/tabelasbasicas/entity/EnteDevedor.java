package br.jus.tjap.precatorio.modulos.tabelasbasicas.entity;

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
}

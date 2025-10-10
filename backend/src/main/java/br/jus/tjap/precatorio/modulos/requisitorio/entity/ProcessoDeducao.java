package br.jus.tjap.precatorio.modulos.requisitorio.entity;

import br.jus.tjap.precatorio.modulos.requisitorio.dto.ProcessoDeducaoDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.Type;

import java.io.Serializable;
import java.util.Map;

@Entity
@Table(schema = "precatorio", name = "processo_deducao")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
public class ProcessoDeducao implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "id_tipo_deducao")
    private Integer tipoDeducao;

    @Column(name = "numero_processo_origem", nullable = false, length = 20)
    private String numeroProcessoOrigem;

    @Column(name = "nome_pessoa_origem", nullable = false, length = 255)
    private String nomePessoaOrigem;

    @Column(name = "documento_pessoa_origem", length = 255)
    private String documentoPessoaOrigem;

    @Column(name = "numero_processo_destino", nullable = false, length = 20)
    private String numeroProcessoDestino;

    @Column(name = "nome_pessoa_destino", nullable = false, length = 255)
    private String nomePessoaDestino;

    @Column(name = "documento_pessoa_destino", length = 255)
    private String documentoPessoaDestino;

    @Column(name = "numero_documento_requisicao_deducao", length = 255)
    private String numeroDocumentoRequisicaoDeducao;

    @Column(name = "dados_deducao", columnDefinition = "jsonb")
    private Map<String, Object> dadosDeducao;

    public ProcessoDeducaoDTO toDto(){
        ProcessoDeducaoDTO resultado = new ProcessoDeducaoDTO();

        resultado.setId(this.id);
        resultado.setNumeroProcessoOrigem(this.numeroProcessoOrigem);
        resultado.setTipoDeducao(this.tipoDeducao);
        resultado.setDadosDeducao(this.dadosDeducao);

        return resultado;
    }

}
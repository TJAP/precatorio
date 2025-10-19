package br.jus.tjap.precatorio.modulos.requisitorio.dto;

import br.jus.tjap.precatorio.util.FlexibleLocalDateDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DadosDeducaoDTO {

    private String banco;
    private String conta;
    private String agencia;
    private String dv;
    private Object valor;
    private Object valorDeducao;
    private String tributacao_irrf;
    private Object percentual_honorarios;
    private Object outros_valores_honorarios;
    private String observacao;
    private String descricao;
    private Integer tipoConta;
    private Object porcentagemCessao;
    @JsonDeserialize(using = FlexibleLocalDateDeserializer.class)
    private LocalDate data_nascimento_pessoa_destino;


    // defesa contra tipos diferentes de bigdecimal vindos da requisição
    public BigDecimal getValor() {
        if (valor == null) {
            return BigDecimal.ZERO;
        }

        if (valor instanceof BigDecimal) {
            return (BigDecimal) valor;
        }

        if (valor instanceof Number) {
            return BigDecimal.valueOf(((Number) valor).doubleValue());
        }

        if (valor instanceof String) {
            String valore = ((String) valor).trim().replace(",", ".");
            try {
                return new BigDecimal(valore);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Valor inválido para valor: " + valor, e);
            }
        }

        throw new IllegalArgumentException("Tipo inválido para valor: " + valor.getClass());
    }

    public BigDecimal getValorDeducao() {
        if (valorDeducao == null) {
            return BigDecimal.ZERO;
        }

        if (valorDeducao instanceof BigDecimal) {
            return (BigDecimal) valorDeducao;
        }

        if (valorDeducao instanceof Number) {
            return BigDecimal.valueOf(((Number) valorDeducao).doubleValue());
        }

        if (valorDeducao instanceof String) {
            String valor = ((String) valorDeducao).trim().replace(",", ".");
            try {
                return new BigDecimal(valor);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Valor inválido para valorDeducao: " + valor, e);
            }
        }

        throw new IllegalArgumentException("Tipo inválido para valorDeducao: " + valorDeducao.getClass());
    }

    public BigDecimal getOutros_valores_honorarios() {
        if (outros_valores_honorarios == null) {
            return BigDecimal.ZERO;
        }

        if (outros_valores_honorarios instanceof BigDecimal) {
            return (BigDecimal) outros_valores_honorarios;
        }

        if (outros_valores_honorarios instanceof Number) {
            return BigDecimal.valueOf(((Number) outros_valores_honorarios).doubleValue());
        }

        if (outros_valores_honorarios instanceof String) {
            String valor = ((String) outros_valores_honorarios).trim().replace(",", ".");
            try {
                return new BigDecimal(valor);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Valor inválido para outros_valores_honorarios: " + valor, e);
            }
        }

        throw new IllegalArgumentException("Tipo inválido para outros_valores_honorarios: " + outros_valores_honorarios.getClass());
    }

    public BigDecimal getPorcentagemCessao() {
        if (porcentagemCessao == null) {
            return BigDecimal.ZERO;
        }

        if (porcentagemCessao instanceof BigDecimal) {
            return (BigDecimal) porcentagemCessao;
        }

        if (porcentagemCessao instanceof Number) {
            return BigDecimal.valueOf(((Number) porcentagemCessao).doubleValue());
        }

        if (porcentagemCessao instanceof String) {
            String valor = ((String) porcentagemCessao).trim().replace(",", ".");
            try {
                return new BigDecimal(valor);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Valor inválido para porcentagemCessao: " + valor, e);
            }
        }

        throw new IllegalArgumentException("Tipo inválido para porcentagemCessao: " + porcentagemCessao.getClass());
    }

    public BigDecimal getPercentual_honorarios() {
        if (percentual_honorarios == null) {
            return BigDecimal.ZERO;
        }

        if (percentual_honorarios instanceof BigDecimal) {
            return (BigDecimal) percentual_honorarios;
        }

        if (percentual_honorarios instanceof Number) {
            return BigDecimal.valueOf(((Number) percentual_honorarios).doubleValue());
        }

        if (percentual_honorarios instanceof String) {
            String valor = ((String) percentual_honorarios).trim().replace(",", ".");
            try {
                return new BigDecimal(valor);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Valor inválido para percentual_honorarios: " + valor, e);
            }
        }

        throw new IllegalArgumentException("Tipo inválido para percentual_honorarios: " + percentual_honorarios.getClass());
    }

}


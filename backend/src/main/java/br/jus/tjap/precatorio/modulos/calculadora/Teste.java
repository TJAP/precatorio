package br.jus.tjap.precatorio.modulos.calculadora;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Objects;

public class Teste {

    // ====== DTO ======
    public static class DadosCalculoDTO {
        private final String tipoPessoa; // "CPF" ou "CNPJ"
        private final String vinculo;    // "Efetivo", "Comissionado", "Sem Vinculo", "Indenização", "PJ-CESSÃO M.O", ...
        private final boolean rraNoRequisitorio;           // coluna 3 (RRA no requisitório) -> SIM/NÃO
        private final boolean campoPrevidenciaPreenchido;  // coluna 4 (campo previdência preenchido) -> SIM/NÃO

        public DadosCalculoDTO(String tipoPessoa, String vinculo, boolean rraNoRequisitorio, boolean campoPrevidenciaPreenchido) {
            this.tipoPessoa = tipoPessoa;
            this.vinculo = vinculo;
            this.rraNoRequisitorio = rraNoRequisitorio;
            this.campoPrevidenciaPreenchido = campoPrevidenciaPreenchido;
        }

        public String getTipoPessoa() { return tipoPessoa; }
        public String getVinculo() { return vinculo; }
        public boolean isRraNoRequisitorio() { return rraNoRequisitorio; }
        public boolean isCampoPrevidenciaPreenchido() { return campoPrevidenciaPreenchido; }
    }

    // ====== Enums ======
    public enum TipoCalculo { CALCULO_A, CALCULO_A1, CALCULO_B, CALCULO_B1, CALCULO_B2, CALCULO_C }
    public enum PrevidenciaDestino { AMPREV, INSS, NENHUM }
    public enum TributacaoIR { RRA, ALIQUOTA_EFETIVA, ISENTO, TRIBUTACAO_PJ_1, TRIBUTACAO_PJ_1_5 }

    // ====== Resultado ======
    public static class ResultadoCalculo {
        private final TipoCalculo tipoCalculo;
        private final PrevidenciaDestino previdenciaDestino;
        private final TributacaoIR tributacaoIR;

        public ResultadoCalculo(TipoCalculo tipoCalculo, PrevidenciaDestino previdenciaDestino, TributacaoIR tributacaoIR) {
            this.tipoCalculo = tipoCalculo;
            this.previdenciaDestino = previdenciaDestino;
            this.tributacaoIR = tributacaoIR;
        }

        public TipoCalculo getTipoCalculo() { return tipoCalculo; }
        public PrevidenciaDestino getPrevidenciaDestino() { return previdenciaDestino; }
        public TributacaoIR getTributacaoIR() { return tributacaoIR; }

        @Override
        public String toString() {
            return "ResultadoCalculo{" +
                    "tipoCalculo=" + tipoCalculo +
                    ", previdenciaDestino=" + previdenciaDestino +
                    ", tributacaoIR=" + tributacaoIR +
                    '}';
        }
    }

    // ====== Service (métodos principais) ======
    public static ResultadoCalculo determinarResultado(DadosCalculoDTO dados) {
        Objects.requireNonNull(dados, "dados não pode ser nulo");
        String tipoPessoa = safeUpper(dados.getTipoPessoa());
        String vinculoNorm = normalizeAndCompact(dados.getVinculo());

        // 1) Previdência
        PrevidenciaDestino previdencia = determinarPrevidencia(tipoPessoa, vinculoNorm, dados.isCampoPrevidenciaPreenchido());

        // 2) Tributação IR
        TributacaoIR tributacao = determinarTributacaoIR(tipoPessoa, vinculoNorm, dados.isRraNoRequisitorio());

        // 3) Tipo de cálculo (usa principalmente tipoPessoa, vinculo e tributacao)
        TipoCalculo tipoCalculo = determinarTipoCalculo(tipoPessoa, vinculoNorm, tributacao);

        return new ResultadoCalculo(tipoCalculo, previdencia, tributacao);
    }

    private static PrevidenciaDestino determinarPrevidencia(String tipoPessoa, String vinculoNorm, boolean campoPrevidenciaPreenchido) {
        if ("CPF".equals(tipoPessoa)) {
            if (campoPrevidenciaPreenchido) {
                if (vinculoNorm.contains("EFETIVO")) {
                    return PrevidenciaDestino.AMPREV; // Efetivo + campoPrev = AMPREV
                } else if (vinculoNorm.contains("COMISSIONADO")) {
                    return PrevidenciaDestino.INSS;   // Comissionado + campoPrev = INSS
                }
            }
            // para outros vínculos ou quando campoPrev = false -> NENHUM
            return PrevidenciaDestino.NENHUM;
        }
        // CNPJ sempre NENHUM no campo previsão da tabela
        return PrevidenciaDestino.NENHUM;
    }

    private static TributacaoIR determinarTributacaoIR(String tipoPessoa, String vinculoNorm, boolean rraNoRequisitorio) {
        if ("CPF".equals(tipoPessoa)) {
            if (vinculoNorm.contains("INDENIZACAO") || vinculoNorm.contains("INDENIZACAO".toUpperCase(Locale.ROOT))) {
                return TributacaoIR.ISENTO; // CPF indenização -> isento
            }
            if (rraNoRequisitorio) {
                return TributacaoIR.RRA;
            } else {
                return TributacaoIR.ALIQUOTA_EFETIVA;
            }
        } else { // CNPJ
            if (vinculoNorm.contains("PJ CESSAO") || vinculoNorm.contains("PJCESSAO") || vinculoNorm.contains("PJ CESSAO M O")) {
                return TributacaoIR.TRIBUTACAO_PJ_1; // 1%
            } else if (vinculoNorm.contains("SERVICOS") || vinculoNorm.contains("SERVICO")) {
                return TributacaoIR.TRIBUTACAO_PJ_1_5; // 1.5%
            } else {
                // PJ-Outros, Simples Nacional, Indenização (PJ) -> ISENTO
                return TributacaoIR.ISENTO;
            }
        }
    }

    private static TipoCalculo determinarTipoCalculo(String tipoPessoa, String vinculoNorm, TributacaoIR tributacaoIR) {
        if ("CPF".equals(tipoPessoa)) {
            if (vinculoNorm.contains("EFETIVO") || vinculoNorm.contains("COMISSIONADO")) {
                // para efetivo/comissionado: diferenciar por tributacao (RRA => A ; ALIQUOTA => A1)
                if (tributacaoIR == TributacaoIR.RRA) return TipoCalculo.CALCULO_A;
                if (tributacaoIR == TributacaoIR.ALIQUOTA_EFETIVA) return TipoCalculo.CALCULO_A1;
                // caso inesperado (ex.: ISENTO) - tratar como erro previsível:
                throw new IllegalArgumentException("Combinação CPF + " + vinculoNorm + " com tributação inesperada: " + tributacaoIR);
            } else if (vinculoNorm.contains("SEM VINCULO") || vinculoNorm.contains("SEMVINCULO")) {
                return TipoCalculo.CALCULO_B;
            } else if (vinculoNorm.contains("INDENIZACAO")) {
                return TipoCalculo.CALCULO_C;
            } else {
                throw new IllegalArgumentException("Vínculo CPF não reconhecido: " + vinculoNorm);
            }
        } else if ("CNPJ".equals(tipoPessoa)) {
            if (vinculoNorm.contains("PJ CESSAO") || vinculoNorm.contains("PJCESSAO")) {
                return TipoCalculo.CALCULO_B2;
            } else if (vinculoNorm.contains("SERVICOS") || vinculoNorm.contains("SERVICO")) {
                return TipoCalculo.CALCULO_B1;
            } else {
                // PJ-OUTROS, SIMPLES NACIONAL, INDENIZAÇÃO (PJ), etc.
                return TipoCalculo.CALCULO_C;
            }
        } else {
            throw new IllegalArgumentException("Tipo de pessoa inválido: " + tipoPessoa);
        }
    }

    // ====== Helpers ======
    private static String safeUpper(String s) {
        return s == null ? "" : s.trim().toUpperCase(Locale.ROOT);
    }

    /**
     * Normaliza (remove acentuação), deixa em maiúsculas e compacta caracteres especiais
     * para facilitar comparações "fuzzy" com os valores da tabela.
     */
    private static String normalizeAndCompact(String s) {
        if (s == null) return "";
        String up = s.trim().toUpperCase(Locale.ROOT);
        // remove acentos
        String noAccent = Normalizer.normalize(up, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        // substitui tudo que não for letra/dígito por espaço e compacta espaços
        String compact = noAccent.replaceAll("[^A-Z0-9 ]", " ").replaceAll("\\s+", " ").trim();
        return compact;
    }

    // ====== Exemplo de uso / Teste ======
    public static void main(String[] args) {
        DadosCalculoDTO[] casos = new DadosCalculoDTO[] {
                // CPF Efetivo SIM SIM -> AMPREV, RRA -> CALCULO A
                new DadosCalculoDTO("CPF", "Efetivo", true, true),
                // CPF Comissionado SIM SIM -> INSS, RRA -> CALCULO A
                new DadosCalculoDTO("CPF", "Comissionado", true, true),
                // CPF Efetivo NÃO SIM -> AMPREV, ALIQUOTA EFETIVA -> CALCULO A1
                new DadosCalculoDTO("CPF", "Efetivo", false, true),
                // CPF Comissionado NÃO NÃO -> -, ALIQUOTA EFETIVA -> CALCULO A1
                new DadosCalculoDTO("CPF", "Comissionado", false, false),
                // CPF Sem Vinculo NÃO NÃO -> -, ALIQUOTA EFETIVA -> CALCULO B
                new DadosCalculoDTO("CPF", "Sem Vinculo", false, false),
                // CPF INDENIZAÇÃO NÃO NÃO -> -, ISENTO -> CALCULO C
                new DadosCalculoDTO("CPF", "INDENIZAÇÃO", false, false),
                // CNPJ PJ-CESSÃO M.O
                new DadosCalculoDTO("CNPJ", "PJ-CESSÃO M.O", false, false),
                // CNPJ PJ-P. SERVIÇOS
                new DadosCalculoDTO("CNPJ", "PJ-P. SERVIÇOS", false, false),
                // CNPJ SIMPLES NACIONAL
                new DadosCalculoDTO("CNPJ", "SIMPLES NACIONAL", false, false)
        };

        for (DadosCalculoDTO d : casos) {
            ResultadoCalculo r = determinarResultado(d);
            System.out.println("Input: " + d.getTipoPessoa() + " | " + d.getVinculo()
                    + " | RRA=" + d.isRraNoRequisitorio() + " | CampoPrev=" + d.isCampoPrevidenciaPreenchido());
            System.out.println(" -> " + r);
            System.out.println("-------------------------------------------------");
        }
    }
}

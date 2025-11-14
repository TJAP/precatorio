# 🏛️ Serviço de Precatórios – Backend

[![Java](https://img.shields.io/badge/Java-21-red?logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.6-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-blue?logo=postgresql)](https://www.postgresql.org/)
[![OAuth2](https://img.shields.io/badge/Security-OAuth2-yellow?logo=springsecurity)](https://spring.io/projects/spring-security)
[![License](https://img.shields.io/badge/Licença-Institucional-lightgrey)](#📚-licença-e-direitos)
[![Build](https://img.shields.io/badge/Build-Maven-orange?logo=apachemaven)](https://maven.apache.org/)

---

## 📖 Descrição Geral

> Sistema backend responsável pelo **gerenciamento de Precatórios e RPVs**, com cálculos automatizados, controle de parâmetros e geração de **alvarás em PDF** conforme as normas da **Resolução 303/2021 do CNJ** e **EC 113/2021**.

---

## 🧠 Estrutura de Pacotes
br.jus.tjap.precatorio
├── config          # Segurança, CORS, OAuth2
├── controller      # Endpoints REST
├── service         # Regras de negócio
├── repository      # JPA Repositories
├── model/entity    # Entidades do banco
├── dto             # Objetos de transporte
├── enums           # Constantes e tipos fixos
└── util            # Funções auxiliares

---

## 🧩 Estrutura Modular

### 🔹 **Módulo Repositório**
Gerencia os **requisitórios do tipo Precatório e RPV**:
- Cadastro completo de dados do credor, processo e ente devedor;
- Classificação por natureza (alimentar, comum, indenizatório);
- Registro de movimentações e histórico de cálculos.

---

### 🔹 **Módulo Parâmetros e Tabelas Básicas**
CRUDs e tabelas de suporte ao cálculo:
- **Índices econômicos:** IPCA, IPCA-E, SELIC, Poupança;
- **Tabelas IRRF e INSS** (faixas progressivas);
- **Tipos de cálculo, tributos e parâmetros do sistema**;
- Versionamento via **Flyway**.

---

### 🔹 **Módulo Cálculo do Requisitório**
Executa a lógica de **atualização monetária e pagamento**:
#### 🧮 Atualização
- Correção conforme índices oficiais e períodos de graça;
- Reaplicação em pagamentos parciais.

#### 💰 Pagamento
- Cálculo de **honorários**, **deságio**, **parcial** e **total**;
- Cálculo de **tributos (INSS e IRRF)**.

#### 📄 Geração de Documentos
- Emissão de **Alvará de Pagamento em PDF** (JasperReports/iText);
- Assinaturas eletrônicas e brasão institucional.

---

## 🛠️ Tecnologias Principais

| Tecnologia | Versão | Descrição |
|-------------|---------|-----------|
| ☕ **Java** | 21 | Plataforma principal (LTS) |
| 🌱 **Spring Boot** | 3.5.6 | Framework base |
| 🧩 **Spring Data JPA** | 3.5.6 | ORM e persistência |
| 🔒 **Spring Security + OAuth2** | 3.5.6 | Autenticação e autorização |
| 🐘 **PostgreSQL** | 15+ | Banco relacional |
| 📄 **JasperReports / iText** | 7.0.3 | Geração de relatórios PDF |
| 🧭 **Flyway** | 10+ | Versionamento do banco |
| 🔁 **ModelMapper** | 3.x | Mapeamento DTO ↔ Entidade |

---

## 🔐 Segurança e Autenticação

> Implementação baseada em **OAuth2 Resource Server**, integrada a provedores como **Keycloak**.  
> Todas as rotas exigem **JWT válido**, com escopos de acesso configurados por perfil.

**Perfis disponíveis:**
- `ROLE_ADMIN` – acesso completo  
- `ROLE_GESTOR` – gestão e cálculos  
- `ROLE_ANALISTA` – parametrização e relatórios  
- `ROLE_USUARIO` – consulta e acompanhamento

---

## 🧱 Banco de Dados

Estrutura principal no schema `precatorio`:

---

## 📚 Licença e Direitos

> 📌 Tribunal de Justiça do Estado do Amapá (TJAP)
> Sistema desenvolvido conforme diretrizes do Conselho Nacional de Justiça (CNJ).
> Distribuição e uso restritos ao âmbito institucional.


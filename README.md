# 🚚 Transportadora — Sistema de Gestão de Transporte (Backend)

API REST para gestão completa das operações de uma transportadora: cadastro de clientes, motoristas e veículos, controle de cargas e notas fiscais, faturamento, roteirização de entregas e um portal exclusivo para o cliente consultar suas próprias notas.

Projeto desenvolvido para praticar arquitetura em camadas no Spring Boot e modelagem de um domínio de negócio real, com autenticação, geração de relatórios e integrações.

🔗 Front-end deste projeto: [TransportadoraFrontEnd](https://github.com/AprendizR/TransportadoraFrontEnd)

## 🛠 Tecnologias

- **Java 21** + **Spring Boot 3.5.9**
- **Spring Web** — exposição da API REST
- **Spring Data JPA** — persistência e relacionamento entre entidades
- **Spring Security + JWT** — autenticação stateless, senhas com BCrypt
- **PostgreSQL** — banco de dados relacional
- **Apache POI** — exportação de relatórios para Excel
- **iText7** — geração de PDFs (romaneio e relatório de nota fiscal)
- **Lombok** — redução de boilerplate

## ⚙️ Funcionalidades

**Autenticação & Usuários**
- Login com JWT e registro de novos usuários
- Listagem de usuários e exclusão restrita a administrador

**Cadastros**
- CRUD de Clientes, Motoristas e Veículos
- Busca/autocomplete de motoristas e veículos
- Validação de CPF, CNPJ e telefone com anotações customizadas (`@ValidCPF`, `@ValidCNPJ`, `@ValidTelefone`)

**Cargas**
- Criação de carga vinculando motorista, veículo e notas fiscais
- Adição e remoção de notas em uma carga já existente
- Registro de ocorrências vinculadas a uma carga/nota
- Geração de romaneio em PDF
- Roteirização automática: ordena as entregas por proximidade, geocodificando cidades sem coordenadas

**Notas Fiscais**
- CRUD completo, com filtros dinâmicos via JPA Specification (ex: notas disponíveis para uma nova carga)
- Upload e remoção de foto comprobatória de entrega
- Baixa e cancelamento de baixa
- Geração de relatório individual em PDF

**Faturamento & Dashboard**
- Relatório de faturamento agrupado por cliente e por cidade
- Exportação do faturamento para Excel
- Dashboard com estatísticas gerais do sistema

**Folha do Motorista**
- Cálculo de folha por motorista, com ajuste de descontos e de dias trabalhados

**Portal do Cliente**
- Login separado, isolado do sistema interno
- Consulta das próprias notas fiscais

## 🏗 Arquitetura

O projeto segue o fluxo **Controller → Service → Repository**, com:
- **DTOs de request/response** separando o que entra e sai da API do modelo de domínio
- **Mappers** dedicados para conversão entre entidade e DTO
- **GlobalExceptionHandler** centralizando o tratamento de erros da aplicação
- Endpoints públicos (`/api/auth/**`, `/api/portal/**`) isolados dos protegidos por JWT via `SecurityConfig`

## ▶️ Como executar

1. Clone o repositório:
   ```bash
   git clone https://github.com/AprendizR/TransporadoraBackEnd.git
   ```
2. Crie um banco PostgreSQL chamado `transportadora_db`.
3. Configure as credenciais do banco e o segredo do JWT em `application.properties` (ou, preferencialmente, em variáveis de ambiente).
4. Execute a aplicação:
   ```bash
   mvn spring-boot:run
   ```
5. A API estará disponível em `http://localhost:8080`

> **Nota de segurança:** as credenciais no `application.properties` deste repositório são valores de desenvolvimento local.

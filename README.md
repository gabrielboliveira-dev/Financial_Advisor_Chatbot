# 🤖 Financial Advisor Chatbot
_Descomplicando investimentos com inteligência artificial e uma arquitetura robusta._

## 🎯 Sobre o Projeto

O mercado financeiro é frequentemente complexo e inacessível para a maioria das pessoas, criando uma barreira significativa para quem deseja investir com segurança e inteligência. Este projeto aborda essa dor desenvolvendo um **assistente de investimentos pessoal** na forma de um chatbot para Telegram.

Utilizando uma arquitetura de microsserviços e aplicando princípios de engenharia de software de alta qualidade (Clean Architecture, SOLID), o bot vai além de simplesmente fornecer dados. Ele oferece:
* **Análise de Portfólio:** Visão detalhada dos ativos.
* **Gestão de Carteira:** Ferramentas para adicionar, remover e acompanhar investimentos.
* **Análise de Perfil de Risco:** Determinação do perfil do investidor através de um quiz interativo.
* **Sugestões Personalizadas:** Recomendações de ativos para estudo baseadas no perfil.

Tudo isso é entregue através de uma interface conversacional intuitiva no Telegram, capacitando usuários a tomar decisões financeiras mais informadas e inteligentes.

## 🛠️ Tecnologias e Ferramentas

O projeto é construído sobre uma stack tecnológica moderna, dividindo responsabilidades entre o ecossistema Java e Python:

* **Linguagens:** Java 17 & Python 3.9
    * *Justificativa:* Java oferece a robustez, tipagem forte e performance necessárias para o *core* de regras de negócios. Python foi escolhido pela sua imensa facilidade e ecossistema maduro para manipulação de dados financeiros e geração de gráficos.
* **Framework Backend:** Spring Boot 3.5
    * *Justificativa:* Framework padrão da indústria que acelera a criação da API, injeção de dependências e configuração do servidor de forma robusta, mantendo o domínio limpo.
* **Micro-framework de Análise:** Flask
    * *Justificativa:* Leve e direto ao ponto, ideal para expor os scripts de análise de dados do Python rapidamente como uma API REST interna consumida pelo Java.
* **Comunicação Externa:** Spring WebFlux (`WebClient`) & Telegram Bots API
    * *Justificativa:* O `WebClient` permite chamadas assíncronas e não-bloqueantes de alta performance para a API de cotações, enquanto a API do Telegram gerencia toda a interface conversacional com o usuário de forma nativa.
* **Resiliência e Tolerância a Falhas:** Resilience4j (Circuit Breaker)
    * *Justificativa:* Protege o sistema contra instabilidades de APIs financeiras externas. Se o provedor de cotações falhar, o *Circuit Breaker* abre, impedindo travamentos em cascata e retornando respostas de *fallback* amigáveis ao usuário.
* **Processamento de Dados e Visualização:** Pandas & Matplotlib
    * *Justificativa:* Ferramentas fundamentais no Python para manipular rapidamente matrizes de dados de portfólio e renderizar o gráfico de pizza visualizando a diversificação setorial.
* **Banco de Dados:** PostgreSQL 15
    * *Justificativa:* Banco de dados relacional escalável e confiável, excelente para garantir a integridade dos dados (ACID) na modelagem das carteiras e perfis dos usuários.
* **Persistência:** Spring Data JPA / Hibernate
    * *Justificativa:* Abstrai o boilerplate de consultas SQL e do JDBC, facilitando o mapeamento das entidades de infraestrutura para o banco de dados.
* **Contêineres:** Docker & Docker Compose
    * *Justificativa:* Padroniza totalmente o ambiente. Permite orquestrar e subir o banco de dados, o backend Java e o serviço Python simultaneamente em uma rede isolada com um único comando.
* **Testes:** JUnit 5, Mockito & H2 Database
    * *Justificativa:* Ferramentas padrão para garantir que as regras de negócio funcionem isoladas. O banco H2 em memória permite rodar testes de integração complexos de forma extremamente rápida sem depender do PostgreSQL.
* **CI/CD:** GitHub Actions
    * *Justificativa:* Pipeline automatizado na nuvem para validação de builds (Java e Python), execução de testes e verificação de integridade dos contêineres a cada novo commit.
  
## 📐 Arquitetura

O projeto é estruturado em uma **arquitetura de microsserviços**, dividindo a aplicação em dois componentes principais: o "Cérebro" (backend Java) e o "Analista de Dados" (microsserviço Python).

É fortemente influenciado pela **Clean Architecture**, garantindo uma separação clara de responsabilidades entre as camadas (Domain, Application, Infrastructure). Isso torna a lógica de negócios independente de frameworks e tecnologias externas, facilitando a testabilidade, a manutenibilidade e a evolução do sistema.

Os **princípios SOLID** foram rigorosamente aplicados para promover um design de software desacoplado, coeso e extensível. A **resiliência** é uma preocupação central, com a implementação de **Circuit Breakers** (Resilience4j) para proteger o sistema contra falhas e latência em APIs externas, melhorando a disponibilidade e a experiência do usuário.

## ✨ Funcionalidades Implementadas

O chatbot oferece as seguintes funcionalidades, acessíveis através de comandos intuitivos no Telegram:

| Comando / Frase de Exemplo | Ação do Bot | Status |
| :--- | :--- | :--- |
| `fazer quiz de perfil` | Inicia o questionário interativo para definir o perfil de investidor do usuário. | ✅ Implementado |
| `me dê uma sugestão` | Sugere ativos para estudo com base no perfil de risco definido. | ✅ Implementado |
| `adicionar 100 PETR4` | Adiciona um ativo à carteira, buscando o preço atual em tempo real. | ✅ Implementado |
| `adicionar 100 PETR4 a 38.50` | Adiciona um ativo à carteira definindo um preço médio específico. | ✅ Implementado |
| `ver minha carteira` | Mostra um resumo de todos os ativos que o usuário possui. | ✅ Implementado |
| `remover PETR4` | Remove completamente um ativo da carteira. | ✅ Implementado |
| `qual o meu desempenho?` | Calcula e exibe a performance atual da carteira (lucro/prejuízo, rentabilidade percentual). | ✅ Implementado |
| `analisar diversificação` | Gera e envia uma imagem com um gráfico de pizza da diversificação por setor. | ✅ Implementado |
| `cotação da PETR4` | Mostra as informações e o preço atual de um ativo específico. | ✅ Implementado |
| `quanto está ATIVO_INEXISTENTE` | Informa que o ativo não foi encontrado, com uma mensagem de erro específica. | ✅ Implementado |

**Principais Características:**
* **Integração Contínua Automática (CI):** Pipeline configurado no GitHub Actions para rodar testes isolados na nuvem e validar a construção das imagens Docker de todos os microsserviços.
* **Análise de Ativos em Tempo Real:** Fornece cotações e dados de ativos via API externa (Brapi), com fallback de segurança e Circuit Breaker.
* **Gestão de Portfólio Completa:** Permite adicionar, visualizar e remover ativos da carteira, com cálculo automático de preço médio ponderado.
* **Cálculo de Performance:** Calcula o valor total investido, o valor atual, o lucro/prejuízo e a rentabilidade percentual da carteira.
* **Análise de Diversificação Visual:** Comunica-se com um microsserviço Python para gerar e exibir um gráfico de pizza da diversificação da carteira por setor.
* **Análise de Perfil de Risco (Quiz):** Conduz um questionário de múltiplos passos para determinar o perfil de investidor do usuário (Conservador, Moderado, Arrojado).
* **Sugestão de Ativos Personalizada:** Oferece sugestões básicas de ativos para estudo, personalizadas de acordo com o perfil de risco do usuário.

## 🚀 Como Rodar o Projeto

**Pré-requisitos:**
* `Git` instalado.
* `Docker` e `Docker Compose` instalados e em execução.

**Passos:**

1.  **Clone o repositório:**
    ```bash
    git clone [https://github.com/gabrielboliveira-dev/Financial_Advisor_Chatbot.git](https://github.com/gabrielboliveira-dev/Financial_Advisor_Chatbot.git)
    cd Financial_Advisor_Chatbot
    ```

2.  **Verifique a Estrutura de Pastas:**
    Certifique-se de que o projeto tem as pastas `backend/` e `analysis-service/` na raiz, cada uma contendo o seu respetivo `Dockerfile`.

3.  **Configure as Variáveis de Ambiente:**
    Na pasta raiz do projeto, crie um ficheiro chamado `.env` e cole o seguinte conteúdo, substituindo os seus dados:
    ```env
    # Credenciais do Telegram Bot
    TELEGRAM_BOT_TOKEN=SEU_TOKEN_AQUI
    TELEGRAM_BOT_USERNAME=SEU_USERNAME_DO_BOT

    # Configuração da Base de Dados
    DB_URL=jdbc:postgresql://db:5432/financialdb
    DB_USER=admin
    DB_PASSWORD=secret

    # URL do Serviço de Análise (Python)
    ANALYSIS_SERVICE_URL=http://analysis-service:5001
    ```

4.  **Inicie todos os serviços:**
    A partir da pasta raiz do projeto, execute o comando:
    ```bash
    docker-compose up --build
    ```
    *(Nota: o banco de dados é mapeado na porta 5433 para evitar conflitos com instalações locais).*

    Aguarde até que os logs indiquem que os três serviços (`db`, `analysis-service`, `backend`) estão em execução e prontos.

## 🤝 Como Contribuir

Este projeto é um portfólio e um projeto de aprendizado. Novas ideias e contribuições são bem-vindas! Por favor, abra uma *issue* para discutir o que você gostaria de mudar ou adicionar.

## 📄 Licença

Este projeto é distribuído sob a licença MIT.
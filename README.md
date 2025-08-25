# Financial Advisor Chatbot - Seu Assistente Financeiro Pessoal com IA

## Visão Geral

O mercado financeiro é complexo e intimidador para a maioria das pessoas. A falta de acesso a orientação de qualidade e ferramentas intuitivas cria uma barreira significativa para quem deseja investir com segurança e inteligência.

Este projeto nasceu para resolver essa dor. Nosso objetivo é construir o **assistente de investimentos pessoal mais completo e acessível do mercado**, um chatbot com IA que descomplica as finanças e empodera o usuário a tomar decisões mais inteligentes.

Utilizando uma arquitetura de microsserviços robusta e seguindo os mais altos padrões de engenharia de software (Arquitetura Limpa, Código Limpo, SOLID), este assistente vai além de simplesmente fornecer dados. Ele oferece análise, gerenciamento de portfólio e insights personalizados, tudo através de uma conversa intuitiva no Telegram.

## Principais Funcionalidades

Nossa visão é construir um assistente com as seguintes capacidades:

*  **Análise de Ativos em Tempo Real:** Fornece cotações, dados históricos e indicadores fundamentalistas de ações, FIIs e outros ativos.
*  **Gerenciamento de Portfólio:** Permite que o usuário cadastre e acompanhe o desempenho consolidado da sua carteira de investimentos.
*  **Análise de Perfil de Risco:** Um questionário interativo para entender a tolerância ao risco do usuário (Conservador, Moderado, Arrojado).
*  **Insights e Análise de Carteira:** Avalia a diversificação do portfólio por setor e tipo de ativo, oferecendo sugestões para otimização.
*  **Recomendações Personalizadas (Roadmap Futuro):** Sugere ativos e estratégias de alocação com base no perfil de risco e nos objetivos do usuário.
*  **Inteligência de Mercado:** Busca e resume notícias e fatos relevantes que podem impactar os ativos do usuário.

## Princípios de Design

A excelência técnica é o pilar deste projeto.

* **Arquitetura Limpa (Clean Architecture):** A lógica de negócios é 100% independente de frameworks, bancos de dados ou APIs. Isso garante um sistema testável, flexível e de fácil manutenção.
* **Código Limpo (Clean Code):** Escrevemos código para seres humanos. Clareza, simplicidade e expressividade são prioridades.
* **SOLID:** Os cinco princípios do design orientado a objetos são seguidos à risca para criar um software desacoplado, coeso e extensível.

## Arquitetura do Sistema

O sistema é projetado como uma arquitetura de **microsserviços** para garantir escalabilidade e separação de responsabilidades.

1.  **Interface (Telegram):** O usuário interage com o bot. As mensagens são recebidas pelo serviço de Backend.
2.  **Backend (Java / Spring Boot):** O coração da orquestração.
    * **Camada de Infraestrutura:** Recebe as mensagens do Telegram (`TelegramController`), se comunica com o banco de dados (`PortfolioDatabaseGateway`) e com serviços externos (`FinancialDataGateway`, `NluGateway`).
    * **Camada de Aplicação:** Orquestra os fluxos de negócio através de **Casos de Uso** (ex: `AnalyzePortfolioUseCase`).
    * **Camada de Domínio:** Contém as entidades e regras de negócio mais puras (ex: `Portfolio`, `FinancialAsset`, `User`).
3.  **Serviço de IA (Python / Rasa):** Responsável pela Inteligência.
    * **NLU (Natural Language Understanding):** Recebe textos do usuário (ex: "como está minha carteira hoje?") e extrai a intenção (`visualizar_portfolio`) e as entidades.
    * **Análise de Dados:** Utiliza bibliotecas como Pandas e NumPy para realizar cálculos complexos sobre dados de portfólio e de mercado.
    * **Visualização:** Gera gráficos e tabelas com Matplotlib para serem enviados como imagem ao usuário.

## Tecnologias Utilizadas

| Área                    | Tecnologia                                                                                             |
| ----------------------- | ------------------------------------------------------------------------------------------------------ |
| **Backend** | Java, Spring Boot, Spring WebClient, Spring Data JPA                                                   |
| **Serviço de IA** | Python, Rasa (NLU), Pandas, NumPy, Matplotlib, yfinance                                                |
| **Persistência** | PostgreSQL (ou outro banco de dados relacional)                                                        |
| **Infraestrutura** | Docker, Docker Compose                                                                                 |
| **Mensageria** | Telegram Bot API                                                                                       |

## Como Começar

**Pré-requisitos:**
* Git
* Docker e Docker Compose
* Java (JDK 17 ou superior)

**Passos:**

1.  **Clone o repositório:**
    ```bash
    git clone [https://github.com/seu-usuario/financial-advisor-chatbot.git](https://github.com/seu-usuario/financial-advisor-chatbot.git)
    cd financial-advisor-chatbot
    ```

2.  **Configure as variáveis de ambiente:**
    Crie um arquivo `.env` na raiz do projeto com o seguinte conteúdo:
    ```env
    # Credenciais do Telegram Bot
    TELEGRAM_BOT_TOKEN=SEU_TOKEN_AQUI
    TELEGRAM_BOT_USERNAME=SEU_USERNAME_DO_BOT

    # Configuração do Banco de Dados (Exemplo com PostgreSQL)
    DB_URL=jdbc:postgresql://db:5432/financialdb
    DB_USER=admin
    DB_PASSWORD=secret
    ```

3.  **Inicie os serviços com Docker Compose:**
    ```bash
    docker-compose up --build
    ```
    Este comando irá construir as imagens e iniciar todos os contêineres necessários (Backend, Serviço de IA, Banco de Dados).

## Como Contribuir

Este é um projeto de aprendizado e desenvolvimento. Contribuições são bem-vindas! Por favor, abra uma *issue* para discutir novas ideias ou relate um bug.

## Licença

Este projeto é distribuído sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

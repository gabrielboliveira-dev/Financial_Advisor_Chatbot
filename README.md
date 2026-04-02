# Assistente Financeiro Pessoal com IA

## 🎯 Visão Geral

O mercado financeiro é complexo e intimidador para a maioria das pessoas. A falta de acesso a orientação de qualidade e ferramentas intuitivas cria uma barreira significativa para quem deseja investir com segurança e inteligência.

Este projeto nasceu para resolver essa dor. O objetivo foi construir um **assistente de investimentos pessoal, completo e acessível**, um chatbot com IA que descomplica as finanças e capacita o utilizador a tomar decisões mais inteligentes.

Utilizando uma arquitetura de microsserviços robusta e seguindo os mais altos padrões de engenharia de software (Arquitetura Limpa, Código Limpo, SOLID), este assistente vai além de simplesmente fornecer dados. Ele oferece análise de portfólio, gestão de carteira e insights personalizados, tudo através de uma conversa intuitiva no Telegram.

## 🤖 Como Usar o Bot

Depois de iniciar os serviços com o Docker Compose, encontre o seu bot no Telegram e comece a conversa. Aqui estão alguns comandos que ele entende:

| Comando / Frase de Exemplo | Ação do Bot |
| :--- | :--- |
| `fazer quiz de perfil` | Inicia o questionário interativo para definir o seu perfil de investidor. |
| `me dê uma sugestão` | Sugere ativos para estudo com base no seu perfil de risco definido. |
| `adicionar 100 PETR4` | Adiciona um ativo à sua carteira, buscando o preço atual em tempo real automaticamente. |
| `adicionar 100 PETR4 a 38.50` | Adiciona um ativo à sua carteira definindo um preço médio específico manualmente. |
| `ver minha carteira` | Mostra um resumo de todos os ativos que você possui. |
| `remover PETR4` | Remove completamente um ativo da sua carteira. |
| `qual o meu desempenho?` | Calcula e exibe a performance atual da sua carteira (lucro/prejuízo, etc.). |
| `analisar diversificação` | Gera e envia uma imagem com um gráfico de pizza da sua diversificação por setor. |
| `cotação da PETR4` | Mostra as informações e o preço atual de um ativo específico. |

## ✨ Principais Funcionalidades (Versão 1.1)

* **Análise de Ativos em Tempo Real:** Fornece cotações e dados de ativos via API externa (Brapi), com fallback de segurança caso o serviço esteja offline.
* **Gestão de Portfólio Resiliente:** Permite adicionar, visualizar e remover ativos da carteira. Adições sem preço definido buscam a cotação real do mercado no momento da transação.
* **Cálculo de Preço Médio:** Atualiza automaticamente o preço médio ponderado ao adicionar mais unidades de um ativo existente.
* **Análise de Performance:** Calcula o valor total investido, o valor atual, o lucro/prejuízo e a rentabilidade percentual da carteira.
* **Análise de Diversificação Visual:** Comunica-se com um microsserviço Python para gerar e exibir um gráfico de pizza da diversificação da carteira por setor, parametrizado por um arquivo de configuração JSON expansível.
* **Análise de Perfil de Risco (Quiz):** Conduz um questionário de múltiplos passos para determinar o perfil de investidor do utilizador (Conservador, Moderado, Arrojado).
* **Sugestão de Ativos:** Oferece sugestões básicas de ativos para estudo, personalizadas de acordo com o perfil de risco do utilizador.

## 📐 Princípios de Design e Arquitetura

* **Arquitetura Limpa (Clean Architecture):** A lógica de negócios é 100% independente de frameworks, bases de dados ou APIs, garantindo um sistema testável e flexível.
* **Código Limpo (Clean Code):** O código foi escrito para ser legível, simples e expressivo, priorizando a manutenibilidade.
* **SOLID:** Os cinco princípios do design orientado a objetos foram a base para criar um software desacoplado, coeso e extensível.
* **Resiliência (Circuit Breaker):** Integração com `Resilience4j` para proteger a aplicação contra falhas ou lentidões de APIs externas de cotação.
* **Microsserviços:** Sistema orquestrado via Docker Compose separando o "Cérebro" (Java) do "Analista de Dados" (Python).

## 🛠️ Tecnologias Utilizadas

| Área | Tecnologia |
| :--- | :--- |
| **Backend** | Java 17 (Eclipse Temurin), Spring Boot 3.5, Spring Data JPA, Spring WebFlux (`WebClient`) |
| **Resiliência** | Resilience4j (Circuit Breaker) |
| **Serviço de IA/Análise** | Python 3.9, Flask, Pandas, Matplotlib |
| **Persistência** | PostgreSQL 15 |
| **Testes** | JUnit 5, Mockito, H2 Database (In-Memory) |
| **Infraestrutura** | Docker, Docker Compose |
| **Mensageria** | Telegram Bot API |

## 🚀 Como Começar

**Pré-requisitos:**
* Git
* Docker e Docker Compose

**Passos:**

1.  **Clone o repositório:**
    ```bash
    git clone https://github.com/gabrielboliveira-dev/Financial_Advisor_Chatbot.git
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

## 🗺️ Roteiro do Projeto

* [✔️] **Fase 1: Fundação e Dados de Ativos (Concluída)**
* [✔️] **Fase 2: Contexto do Usuário & Portfólio (Concluída)**
* [✔️] **Fase 3: Análise e Inteligência (Concluída)**
* [✔️] **Fase 4: Personalização e Recomendações (Concluída)**
* [✔️] **Fase 5: Resiliência, Testes e Flexibilidade (Versão 1.1)**

## 🤝 Como Contribuir

Este projeto é um portfólio e um projeto de aprendizado. Novas ideias e contribuições são bem-vindas! Por favor, abra uma *issue* para discutir o que você gostaria de mudar ou adicionar.

## 📄 Licença

Este projeto é distribuído sob a licença MIT.

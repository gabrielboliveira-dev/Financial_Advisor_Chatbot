# Financial Advisor Chatbot

## Visão Geral

Este projeto é um chatbot financeiro inteligente projetado para fornecer análises e recomendações de investimento de forma acessível e gratuita. Construído com base nos princípios de **Arquitetura Limpa**, **Código Limpo** e **SOLID**, o chatbot utiliza uma arquitetura de microsserviços para garantir escalabilidade, testabilidade e fácil manutenção.

Diferentemente de soluções básicas, este projeto foca em uma experiência de usuário rica, apresentando dados financeiros e análises de forma intuitiva através de um bot do Telegram, com a capacidade de gerar gráficos, tabelas comparativas e insights aprofundados.

## Princípios de Design

O projeto foi meticulosamente desenhado para seguir as melhores práticas de engenharia de software:

  * **Arquitetura Limpa (Clean Architecture):** A lógica de negócios do projeto (Domain e Use Cases) é totalmente independente de frameworks, bancos de dados ou APIs externas. Isso torna o sistema robusto, fácil de testar e flexível para futuras mudanças.
  * **Código Limpo (Clean Code):** Todo o código é escrito para ser legível, fácil de entender e manter. Priorizamos nomes de variáveis e métodos claros, funções concisas e uma estrutura de projeto organizada.
  * **SOLID:** Seguimos os cinco princípios do design orientado a objetos (Single Responsibility, Open/Closed, Liskov Substitution, Interface Segregation, Dependency Inversion) para criar um sistema desacoplado, modular e extensível.

## Tecnologias Utilizadas

Este projeto é construído exclusivamente com ferramentas de código aberto e serviços gratuitos, garantindo total acessibilidade.

### Backend (Java)

  * **Linguagem:** Java
  * **Framework:** Spring Boot
  * **Comunicação Externa:** Spring WebClient
  * **Persistência:** Spring Data JPA (opcional)
  * **Automação de Build:** Maven ou Gradle

### Serviço de IA (Python)

  * **Linguagem:** Python
  * **Framework de IA Conversacional:** Rasa (para NLU e Diálogo)
  * **Análise de Dados:** Pandas e NumPy
  * **Visualização de Dados:** Matplotlib (para gráficos)
  * **APIs de Dados Financeiros:** yfinance (para dados do Yahoo Finance) e Alpha Vantage (plano gratuito)

### Infraestrutura e Mensageria

  * **Containerização:** Docker
  * **Orquestração Local:** Docker Compose
  * **API de Mensageria:** Telegram Bot API
  * **Serviços de Nuvem:** Oracle Cloud Free Tier (opcional para deploy gratuito)

## Arquitetura do Projeto

A arquitetura segue o modelo de microsserviços e a Arquitetura Limpa, com uma clara separação de responsabilidades.

1.  **Interface de Mensageria (Telegram):** O usuário interage com o chatbot através do Telegram. A API do Telegram envia as mensagens para o **Controller** do backend Java.
2.  **Backend (Java):** O `TelegramController` recebe a mensagem e a encaminha para a camada de **Application** (Use Cases).
3.  **Camada de Aplicação:** O `GetFinancialReportUseCase` coordena a lógica. Ele solicita dados financeiros do `FinancialDataGateway` e envia a consulta do usuário para a **API do Rasa** (serviço Python).
4.  **Serviço de IA (Python/Rasa):** A API do Rasa recebe a consulta, utiliza o modelo treinado de PLN para interpretar a intenção do usuário, e gera uma resposta baseada na análise de dados fornecida.
5.  **Resposta:** O backend Java recebe a resposta do Rasa, a formata (incluindo gráficos gerados pelo Python) e a envia de volta ao usuário via API do Telegram.

## Como Começar

Para rodar o projeto localmente, você precisa ter o **Docker** e o **Docker Compose** instalados.

1.  **Clone o Repositório:**
    ```bash
    git clone https://github.com/seu-usuario/financial-advisor-chatbot.git
    cd financial-advisor-chatbot
    ```
2.  **Configure as Variáveis de Ambiente:**
      * No diretório raiz do projeto, crie um arquivo `.env`.
      * Adicione as seguintes variáveis de ambiente, substituindo pelos seus tokens de API:
        ```env
        TELEGRAM_BOT_TOKEN=SEU_TOKEN_DO_BOT_TELEGRAM
        # Opcional, se usar Alpha Vantage em vez de yfinance
        ALPHA_VANTAGE_API_KEY=SUA_CHAVE_ALPHA_VANTAGE
        ```
3.  **Inicie os Contêineres:**
    ```bash
    docker-compose up --build
    ```
    Isso irá construir as imagens Docker e iniciar o backend Java, o serviço Python (com Rasa) e quaisquer outros serviços definidos no arquivo `docker-compose.yml`.

## Uso

Com o bot em execução, abra o Telegram, encontre o seu bot pelo nome de usuário e comece a interagir. Você pode perguntar sobre o ticker de uma empresa, como `PETR4`, ou fazer perguntas mais complexas sobre finanças.

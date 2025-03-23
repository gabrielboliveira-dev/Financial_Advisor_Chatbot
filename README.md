# Financial Advisor Chatbot

## Overview

Este projeto consiste em um chatbot inteligente desenvolvido para fornecer informações e recomendações financeiras aos usuários através do WhatsApp. Utilizando uma arquitetura de microsserviços, o backend em Java Spring Boot coleta dados financeiros de APIs externas e se comunica com um serviço de Inteligência Artificial em Python para gerar análises, perspectivas de investimento e um resumo executivo. A resposta final é então formatada e enviada ao usuário via WhatsApp Business API.

## Key Features

* **Análise Financeira:** Fornece insights sobre empresas com base em dados financeiros atualizados.
* **Resumo Executivo:** Gera um resumo conciso das principais informações financeiras da empresa.
* **Perspectivas de Investimento:** Oferece uma visão sobre as possíveis oportunidades e riscos de investimento.
* **Recomendação Estratégica:** Sugere uma estratégia de investimento com base na análise dos dados.
* **Integração com WhatsApp:** Interação amigável e acessível através da plataforma de mensagens WhatsApp.
* **Arquitetura Escalável:** Design de microsserviços que permite escalabilidade e manutenção independentes.

## Technologies Used

### Backend (Java)

* **Linguagem:** Java
* **Framework:** Spring Boot
* **Web:** Spring Web (RestTemplate ou WebClient)
* **Dados:** Spring Data JPA (opcional para persistência)
* **JSON:** Jackson ou Gson
* **Boilerplate Reduction:** Lombok
* **Logging:** SLF4j e Logback ou Log4j2
* **Testes:** JUnit e Mockito
* **Build:** Maven ou Gradle

### Serviço de IA (Python)

* **Linguagem:** Python
* **Dados:** Pandas
* **Computação Numérica:** NumPy
* **Requisições HTTP:** Requests
* **Inteligência Artificial:** OpenAI API (com a biblioteca `openai`)
* **API:** Flask ou FastAPI

### Outras Ferramentas e Tecnologias

* **Editor de Código:** VS Code
* **Versionamento:** Git e GitHub
* **Containerização:** Docker
* **Orquestração de Containers:** Kubernetes ou Docker Compose
* **Infraestrutura:** AWS (EC2, ECS, EKS, Lambda)
* **APIs de Mensageria:** WhatsApp Business API
* **APIs de Dados Financeiros:** Integração com diversas APIs de bolsa de valores e dados financeiros.

## Architecture

A arquitetura da aplicação é baseada em microsserviços, com os seguintes componentes principais:

1.  **Backend (Java/Spring Boot):**
    * Recebe a requisição do usuário com o código da empresa através da integração com a API do WhatsApp.
    * Utiliza Spring Web (RestTemplate ou WebClient) para fazer chamadas a APIs de dados financeiros e obter informações como Market Cap, Beta, margens, etc.
    * Se comunica com o Serviço de IA (Python) através de chamadas HTTP (usando RestTemplate ou WebClient).
    * Recebe o relatório gerado pelo serviço de IA.
    * Formata a resposta final e a envia para o usuário através da API do WhatsApp Business.

2.  **Serviço de IA (Python):**
    * Expõe uma API (Flask ou FastAPI) para receber os dados financeiros do backend Java.
    * Utiliza as bibliotecas Pandas e NumPy para processar e analisar os dados.
    * Integra-se com a OpenAI API (opcional) para gerar o resumo executivo, perspectivas de investimento e a recomendação.
    * Retorna o relatório gerado para o backend Java em formato JSON.

3.  **WhatsApp Business API:**
    * Utilizada para receber as mensagens dos usuários e enviar as respostas geradas pelo backend.

## Getting Started

Para executar esta aplicação localmente, siga os seguintes passos:

### Prerequisites

* Java Development Kit (JDK) instalado.
* Python instalado.
* Docker instalado.
* Maven ou Gradle instalado (para o backend Java).
* Pip ou Poetry instalado (para o serviço Python).
* Conta no WhatsApp Business API e credenciais.
* Chave de API da OpenAI (se utilizar essa funcionalidade).
* Chaves de API para as APIs de dados financeiros.

### Backend (Java/Spring Boot)

1.  Clone o repositório para o seu ambiente local.
2.  Navegue até o diretório do backend Java.
3.  Utilize o Maven (`mvn clean install`) ou Gradle (`./gradlew build`) para construir o projeto.
4.  Crie um arquivo `application.properties` ou `application.yml` na pasta `src/main/resources` e configure as seguintes propriedades (substitua pelos seus valores):
    ```properties
    # Exemplo de application.properties
    whatsapp.api.token=SEU_TOKEN_WHATSAPP
    whatsapp.api.url=URL_DA_API_WHATSAPP
    financial.data.api.key=SUA_CHAVE_API_FINANCEIRA
    ai.service.url=http://localhost:5000/analyze # URL do serviço Python (exemplo)
    ```
5.  Execute a aplicação Spring Boot a partir da sua IDE ou utilizando o comando `mvn spring-boot:run` ou `./gradlew bootRun`.

### Serviço de IA (Python)

1.  Navegue até o diretório do serviço Python.
2.  Crie um ambiente virtual: `python -m venv venv` (ou `python3 -m venv venv`) e ative-o (`source venv/bin/activate` no Linux/macOS ou `venv\Scripts\activate` no Windows). Se estiver usando Poetry, execute `poetry install`.
3.  Instale as dependências: `pip install -r requirements.txt` (ou `poetry install` se usar Poetry).
4.  Crie um arquivo `.env` e configure as seguintes variáveis de ambiente:
    ```env
    OPENAI_API_KEY=SUA_CHAVE_OPENAI
    ```
5.  Execute a aplicação Python (Flask): `python app.py` ou (FastAPI): `uvicorn main:app --reload`. Certifique-se de que a porta (ex: 5000) corresponde à configuração no backend Java.

### Variáveis de Ambiente

Certifique-se de configurar todas as variáveis de ambiente necessárias para cada serviço (chaves de API, URLs, etc.) de forma segura, preferencialmente utilizando arquivos `.env` (para o serviço Python) e variáveis de ambiente do sistema ou arquivos de configuração (para o backend Java).

## Running the Application

1.  Certifique-se de que o backend Java e o serviço de IA Python estão em execução.
2.  Envie uma mensagem para o número do seu chatbot no WhatsApp com o código da empresa que você deseja analisar (ex: `PETR4`).
3.  O backend Java receberá a mensagem, consultará as APIs de dados financeiros, enviará os dados para o serviço de IA Python, receberá a resposta e a enviará de volta para você via WhatsApp.

## Deployment

Para realizar o deploy da aplicação em um ambiente de produção, você pode utilizar o Docker para containerizar o backend Java e o serviço de IA Python. Utilize o Docker Compose para gerenciar os containers localmente ou uma plataforma de orquestração de containers como Kubernetes (em serviços como AWS EKS, Google Kubernetes Engine ou Azure Kubernetes Service) ou AWS ECS para um ambiente de produção escalável e resiliente.

## Contributing

Contribuições são bem-vindas! Sinta-se à vontade para abrir issues com sugestões de melhorias ou pull requests com correções e novas funcionalidades.

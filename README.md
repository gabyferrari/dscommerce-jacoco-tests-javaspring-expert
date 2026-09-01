# 🧪 DSCommerce — Testes e Cobertura com JaCoCo

Projeto desenvolvido durante a formação **Java Spring Expert**, com foco na implementação e análise de **testes automatizados** e **cobertura de código utilizando JaCoCo** em uma aplicação REST desenvolvida com Java e Spring Boot.

O projeto utiliza o projeto [DSCommerce](https://github.com/gabyferrari/project-dscommerce-javaspring-professional), uma aplicação de e-commerce desenvolvida ao longo da formação, como base para a criação de testes e análise da cobertura das principais regras de negócio da aplicação.

## 🎯 Objetivo

O objetivo deste projeto é aplicar boas práticas de **testes automatizados**, utilizando métricas de cobertura para identificar partes do código que precisam de maior atenção.

A implementação envolve testes voltados principalmente para a camada de serviços, utilizando mocks para isolar dependências e validar diferentes cenários da aplicação.

## Conceitos

Entre os conceitos e ferramentas utilizados no projeto estão:

* JUnit 5
* Mockito
* Spring Boot Test
* Spring Security Test
* Testes unitários
* Mock de dependências
* Testes de diferentes cenários de negócio
* Validação de comportamentos esperados
* Análise de cobertura de código

## 📊 JaCoCo

O projeto utiliza o **JaCoCo (Java Code Coverage)** para medir a cobertura dos testes automatizados.

A ferramenta permite analisar quais partes do código foram executadas durante os testes e identificar trechos que ainda precisam ser cobertos.

O relatório de cobertura é gerado automaticamente durante o processo de build e disponibilizado em:

```text
target/jacoco-report
```

Para gerar o relatório:

```bash
./mvnw clean verify
```

No Windows:

```bash
mvnw.cmd clean verify
```

Após a execução, o relatório HTML pode ser encontrado em:

```text
target/jacoco-report/index.html
```

## 🛠️ Tecnologias utilizadas

* **Java 17**
* **Spring Boot 3.4.4**
* **Spring Data JPA**
* **Spring Web**
* **Spring Security**
* **OAuth2**
* **Bean Validation**
* **H2 Database**
* **JUnit 5**
* **Mockito**
* **Spring Security Test**
* **JaCoCo**
* **Maven**

## 📁 Estrutura do projeto

```text
src
├── main
│   └── java
│       └── com.devsuperior.dscommerce
│           ├── config
│           ├── controllers
│           ├── dto
│           ├── entities
│           ├── services
│           └── util
│
└── test
    └── java
        └── com.devsuperior.dscommerce
            └── testes automatizados
```

## ▶️ Como executar o projeto

### 1. Clone o repositório

```bash
git clone https://github.com/gabyferrari/dscommerce-jacoco-tests-javaspring-expert.git
```

### 2. Entre na pasta do projeto

```bash
cd dscommerce-jacoco-tests-javaspring-expert
```

### 3. Execute os testes

Linux/macOS:

```bash
./mvnw test
```

Windows:

```bash
mvnw.cmd test
```

### 4. Gerar o relatório de cobertura

```bash
./mvnw clean verify
```

No Windows:

```bash
mvnw.cmd clean verify
```

## 📈 Análise de cobertura

O JaCoCo foi configurado para analisar a cobertura das partes relevantes da aplicação, excluindo componentes que não fazem parte do foco dos testes, como configurações, entidades, DTOs, handlers, controllers e outras classes auxiliares.

Dessa forma, a análise fica concentrada principalmente na **lógica de negócio e nos serviços da aplicação**.

## 📚 Aprendizados

Durante o desenvolvimento deste projeto, foram praticados conceitos importantes para o desenvolvimento de aplicações backend com qualidade, como:

* Criação de testes unitários;
* Isolamento de dependências com Mockito;
* Utilização de mocks;
* Testes de regras de negócio;
* Testes envolvendo segurança;
* Organização e legibilidade dos testes;
* Análise de cobertura de código;
* Identificação de trechos não testados;
* Utilização do JaCoCo integrado ao Maven;
* Boas práticas de desenvolvimento orientado à qualidade.

## 📌 Status

Este projeto foi desenvolvido com o objetivo de aprofundar meus conhecimentos em qualidade de software e testes automatizados, complementando meus estudos em desenvolvimento backend com Java e Spring Boot.

## 🔗 Repositório

[GitHub – DSCommerce Jacoco Tests](https://github.com/gabyferrari/dscommerce-jacoco-tests-javaspring-expert)

https://www.linkedin.com/in/gabriellyferrari/

# Bank-System-
Sistema bancário em microsserviços composto por Core Banking em Java (Spring Boot) e motor antifraude em Python via gRPC. Realiza operações PIX com registro contábil de partidas dobradas em Livro-Razão (Ledger) e avaliação de risco das transações em tempo real.

```markdown
# Core Banking & Motor Antifraude Distribuído (gRPC + Spring Boot)

Sistema bancário simplificado composto por um microsserviço de **Core Banking** desenvolvido em Java com Spring Boot[cite: 6] e um **Motor de Detecção de Fraudes** de alto desempenho desenvolvido em Python utilizando gRPC e Protocol Buffers[cite: 1, 2, 3].

---

## Índice

- [Visão Geral da Arquitetura](#visão-geral-da-arquitetura)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Contrato gRPC (Protobuf)](#contrato-grpc-protobuf)
- [Módulos e Regras de Negócio](#módulos-e-regras-de-negócio)
  - [1. Core Banking (Spring Boot)](#1-core-banking-spring-boot)
  - [2. Motor Antifraude (Python gRPC)](#2-motor-antifraude-python-grpc)
- [Como Configurar e Executar](#como-configurar-e-executar)
  - [Executando o Motor Antifraude](#executando-o-motor-antifraude)
  - [Executando o Teste do Cliente gRPC](#executando-o-teste-do-cliente-grpc)
  - [Executando o Core Banking](#executando-o-core-banking)
- [Exemplo de Chamada da API](#exemplo-de-chamada-da-api)

---

## Visão Geral da Arquitetura

O sistema opera sob uma arquitetura de microsserviços distribuídos:

1. **Core Banking Service (Java / Spring Boot):** Expõe endpoints HTTP REST para transações financeiras (como PIX)[cite: 9] e realiza a persistência dos lançamentos contábeis em formato de Livro-Razão (*Ledger*) usando partidas dobradas[cite: 7, 10].
2. **Fraud Detection Engine (Python / gRPC):** Expõe um serviço RPC para avaliar transações em tempo real com base no valor e parâmetros de risco[cite: 1, 2, 3].

---

## Tecnologias Utilizadas

- **Java & Spring Boot:**
  - Spring Data JPA / Hibernate[cite: 7, 8]
  - Spring Web REST[cite: 9]
  - Jakarta Persistence[cite: 7]
- **Python:**
  - `grpcio` e `grpcio-tools`[cite: 2]
  - `protobuf`[cite: 1]
  - `concurrent.futures`[cite: 3]
- **Comunicação Inter-serviços:**
  - Protocol Buffers (`proto3`)[cite: 1]
  - gRPC sobre HTTP/2[cite: 2, 3]

---

## Estrutura do Projeto

```plaintext
├── CoreBankingApplication.java     # Classe principal de inicialização do Spring Boot
├── TransactionController.java      # Controlador REST para operações PIX
├── TransactionService.java         # Regra de negócio de transferência e escrituração contábil
├── Ledger.java                     # Entidade JPA que mapeia o Livro-Razão (tabela ledger)
├── LedgerRepository.java           # Interface Spring Data JPA para persistência de registros
├── fraud_engine.proto              # Definição do serviço e mensagens gRPC/Protobuf
├── fraud_engine_pb2.py             # Código compilado das mensagens Protobuf em Python
├── fraud_engine_pb2_grpc.py        # Código compilado dos stubs e servicers gRPC em Python
├── fraud_server.py                 # Servidor gRPC do motor de análise de risco
└── test_client.py                  # Cliente de teste simulando chamadas ao motor antifraude

```

---

## Contrato gRPC (Protobuf)

O serviço está sob o namespace `banking.fraud` e pacote Java `com.banco.core.grpc.fraud`:

```protobuf
syntax = "proto3";

package banking.fraud;

option java_package = "com.banco.core.grpc.fraud";
option java_multiple_files = true;

message TransactionRequest {
  string transaction_id = 1;
  string account_origin = 2;
  string account_destination = 3;
  double amount = 4;
  int64 timestamp = 5;
  string location_lat_lon = 6;
}

message RiskAnalysisResponse {
  string transaction_id = 1;
  double risk_score = 2;
  bool is_approved = 3;
  string rejection_reason = 4;
}

service FraudDetectionService {
  rpc AnalyzeTransaction (TransactionRequest) returns (RiskAnalysisResponse);
}

```

---

## Módulos e Regras de Negócio

### 1. Core Banking (Spring Boot)

* **Pacote:** `com.banco.core_banking`.


* **Mecanismo de Partidas Dobradas (*Double-Entry Bookkeeping*):** Toda operação de transferência gera dois lançamentos imutáveis na tabela `ledger` vinculados ao mesmo `transactionId`:


* **Débito:** Registrado na conta de origem com o valor negativado (`amount.negate()`).


* **Crédito:** Registrado na conta de destino com o valor positivo.




* **Entidade `Ledger`:** Armazena `id` (UUID), `transaction_id`, `account_id`, `amount` (precisão 19, escala 4) e data de criação `created_at`.



### 2. Motor Antifraude (Python gRPC)

* **Porta padrão:** `50051` com pool de até 10 threads de execução simultânea.


* **Regras de Análise (`AnalyzeTransaction`):**
* **Valores > R$ 10.000,00:** Marcados com score de risco `0.95`, recusados (`is_approved = False`) e sinalizados com o motivo `ANOMALOUS_AMOUNT_BLOCKED`.


* **Valores <= R$ 10.000,00:** Marcados com score de risco base `0.05` e aprovados (`is_approved = True`).





---

## Como Configurar e Executar

### Executando o Motor Antifraude

1. Certifique-se de possuir o Python instalado com as dependências do gRPC:


```bash
pip install grpcio grpcio-tools protobuf

```


2. Caso precise recompilar o arquivo `.proto`:


```bash
python -m grpc_tools.protoc -I. --python_out=. --grpc_python_out=. fraud_engine.proto

```


3. Inicie o servidor gRPC:


```bash
python fraud_server.py

```


*O terminal indicará: `Motor antifraude ativo na porta 50051...*`.



---

### Executando o Teste do Cliente gRPC

Em outro terminal, execute o script cliente para testar a comunicação direta com o motor:

```bash
python test_client.py

```

O cliente enviará uma transação de teste no valor de R$ 15.000,00, recebendo a resposta de bloqueio:

```text
Concetado ao Motor Antifraude...
Transação em análise...

---RESULTADO DA ANÁLISE---
ID da Transação: PIX-999-ABC
Status: BLOQUEADO
Motivo da rejeição:ANOMALOUS_AMOUNT_BLOCKED

```

---

### Executando o Core Banking

1. Compile e inicialize a aplicação Spring Boot:


```bash
./mvnw spring-boot:run

```


*(Ou execute através da classe principal `CoreBankingApplication.java`)*.



---

## Exemplo de Chamada da API

Com a aplicação Spring Boot em execução na porta padrão (8080), realize uma requisição HTTP via navegador ou terminal:

```bash
curl "http://localhost:8080/pix?origem=CONTA-101&destino=CONTA-202&valor=250.00"

```

**Resposta esperada:**

```text
PIX de R$ 250.00 tranferido de CONTA-101 para CONTA-202 . Verifique o banco de dados!

```

```

```

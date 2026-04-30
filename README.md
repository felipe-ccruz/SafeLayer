# SimpleVM

Aplicação acadêmica que simula um painel de gerenciamento de máquinas virtuais.

## Stack

- **Backend** (`BackVM/`): Spring Boot 4.0.6, JPA, Flyway, Java 21
- **Frontend** (`FrontVM/`): JavaFX 21
- **Banco**: PostgreSQL 15 (via Docker)

## Estrutura

```
SimpleVM/
├── BackVM/             # API REST (porta 8080)
├── FrontVM/            # Cliente JavaFX
└── docker-compose.yml  # Apenas o Postgres
```

## Pré-requisitos

- Java 21
- Maven 3.9+
- Docker Desktop

## Como rodar

**1. Subir o banco**

```bash
docker compose up -d
```

Postgres fica exposto em `localhost:5433` (porta 5432 fica livre para um Postgres local).

**2. Rodar o backend**

```bash
cd BackVM
mvn spring-boot:run
```

API disponível em `http://localhost:8080/api/vms`. As migrations do Flyway criam a tabela `virtual_machines` na primeira execução.

**3. Rodar o frontend**

Em outro terminal:

```bash
cd FrontVM
mvn clean javafx:run
```

A janela do VM Manager abre apontando para `http://localhost:8080`.

## Endpoints principais

| Método | Rota                 | Ação                        |
|--------|----------------------|-----------------------------|
| GET    | `/api/vms`           | Lista todas as VMs          |
| POST   | `/api/vms`           | Cria uma VM                 |
| PATCH  | `/api/vms/{id}/start`| Liga a VM                   |
| PATCH  | `/api/vms/{id}/pause`| Pausa a VM                  |
| PATCH  | `/api/vms/{id}/stop` | Desliga a VM                |
| DELETE | `/api/vms/{id}`      | Remove a VM                 |

Erros: VM inexistente retorna `404`; transição de status inválida retorna `422`.

## Funcionalidades

- Criar VM com nome, sistema (Windows 10/11) e perfil de hardware (Fraco/Médio/Forte)
- Ligar, pausar, parar e excluir
- Tela cheia da VM com simulação de wallpaper quando está em execução

# SimpleVM — Contexto do Projeto

## O que é

SimpleVM é um simulador acadêmico de gerenciamento de máquinas virtuais com arquitetura cliente-servidor. Desenvolvido como projeto de NEGÓCIOS DIGITAIS no CESUPA (7° semestre).

## Arquitetura

```
SimpleVM/
├── BackVM/          # Spring Boot REST API
├── FrontVM/         # JavaFX Desktop App
├── docker-compose.yml  # PostgreSQL 15 (porta 5433)
└── CLAUDE.md
```

## Como rodar

1. `docker-compose up -d` — inicia o PostgreSQL
2. `cd BackVM && mvn spring-boot:run` — inicia o backend em localhost:8080
3. `cd FrontVM && mvn javafx:run` — inicia o frontend JavaFX

## Backend (BackVM)

**Stack:** Spring Boot 4.0.6, Java 17, JPA/Hibernate, Flyway, PostgreSQL, Lombok

**Pacote raiz:** `com.felp.backvm`

**Endpoints REST** em `/api/vms`:

| Método | Rota | Ação |
|--------|------|------|
| GET | `/api/vms` | Listar todas as VMs |
| POST | `/api/vms` | Criar VM |
| PATCH | `/api/vms/{id}/start` | Ligar VM |
| PATCH | `/api/vms/{id}/pause` | Pausar VM |
| PATCH | `/api/vms/{id}/stop` | Desligar VM |
| DELETE | `/api/vms/{id}` | Deletar VM |

**Erros:** 404 para VM não encontrada, 422 para transição de estado inválida (mensagens em português).

**Estrutura de pacotes:**
- `controller/` — VirtualMachineController
- `service/` — VirtualMachineService (lógica de negócio + conversão DTO)
- `domain/` — VirtualMachine (entidade JPA), enums: VmStatus, VmProfile, OsType
- `dto/` — CreateVmRequest, VirtualMachineDTO
- `repository/` — VirtualMachineRepository (JpaRepository)
- `exception/` — GlobalExceptionHandler, VmNotFoundException

**Banco de dados:**
- PostgreSQL na porta 5433 (evita conflito com Postgres local na 5432)
- DB: `vmmanager`, User: `vmuser`, Pass: `vmpass`
- Schema gerenciado por Flyway (`V1__create_virtual_machines.sql`)

## Frontend (FrontVM)

**Stack:** JavaFX 21, Java 21, ControlsFX, org.json, Java HttpClient

**Pacote raiz:** `com.felp.frontvm`

**Telas:**
- **MainController** + `main-view.fxml` — Grade de VMs (FlowPane), botão criar, ações por card
- **CriarVmController** + `criar-vm-view.fxml` — Modal de criação (nome, OS, perfil)
- **VmViewerController** + `vm-viewer-view.fxml` — Visualizador fullscreen com wallpaper simulado

**Padrões de UI:**
- CSS inline (sem folhas de estilo externas)
- Ícones SVG via `LucideIcons.java` (lucide.dev)
- Operações assíncronas com `new Thread(...).start()` + `Platform.runLater()`
- Cards coloridos: Windows 10 = azul, Windows 11 = roxo
- Status: verde (RUNNING), âmbar (PAUSED), cinza (STOPPED)

**Configuração:** `ApiConfig.java` define `BASE_URL = "http://localhost:8080"`

## Domínio

**VmStatus:** `RUNNING`, `PAUSED`, `STOPPED`

**VmProfile** (perfis de hardware):
- `WEAK`: 2 CPU, 4 GB RAM, 60 GB disco
- `MEDIUM`: 4 CPU, 8 GB RAM, 100 GB disco
- `STRONG`: 8 CPU, 16 GB RAM, 200 GB disco

**OsType:** `UBUNTU`, `WINDOWS_11`

**Wallpapers** (exibidos no `VmViewerController` quando a VM está RUNNING):
- `FrontVM/src/main/resources/com/felp/frontvm/images/ubuntu.jpeg`
- `FrontVM/src/main/resources/com/felp/frontvm/images/windows-11.jpg`
- Cards na tela principal continuam usando gradientes (laranja para Ubuntu, roxo para Windows 11)

## Histórico de sessões

- **Sessão 1** — Estrutura base do projeto (Maven multi-módulo, Spring Boot, JavaFX)
- **Sessão 2** — Domínio e persistência (entidade JPA, Flyway, repositório, DTOs)
- **Sessão 3** — Endpoints de Ligar, Desligar, Pausar e Deletar
- **Sessão 4** — Correção de bugs
- **Sessão 5** — Telas JavaFX (Main, CriarVM, VmViewer)
- **Sessão 6** — Substituição de Windows 10 por Ubuntu, wallpapers reais centralizados no viewer

## Convenções

- Mensagens de erro e labels de UI em português
- Não rodar comandos no terminal — apenas entregar código; o usuário executa mvn/docker/git
- Sem comentários desnecessários no código
- Lombok para reduzir boilerplate (@Data, @Builder, @RequiredArgsConstructor)

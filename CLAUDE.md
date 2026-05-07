# SimpleVM — Contexto do Projeto

## O que é

SimpleVM é um simulador acadêmico de gerenciamento de máquinas virtuais com arquitetura cliente-servidor. Desenvolvido como projeto de NEGÓCIOS DIGITAIS no CESUPA (7° semestre).

VMs Ubuntu **rodam como containers Docker reais** (kernel Linux de verdade) gerenciados pelo backend. VMs Windows 11 continuam como simulação visual apenas.

## Arquitetura

```
SimpleVM/
├── BackVM/             # Spring Boot REST API
├── FrontVM/            # JavaFX Desktop App
├── docker-compose.yml  # PostgreSQL 15 (porta 5433)
└── CLAUDE.md
```

## Como rodar

1. `docker-compose up -d` — inicia o PostgreSQL
2. `cd BackVM && mvn spring-boot:run` — inicia o backend em localhost:8080
3. `cd FrontVM && mvn javafx:run` — inicia o frontend JavaFX

## Fluxo do app

1. Login/Cadastro → 2. Tela de Planos → 3. VM Manager
- Botões "Meu plano" e "Sair" estão presentes em ambas as telas pós-login
- Cada usuário só vê suas próprias VMs (filtragem via header `X-User-Id`)

## Backend (BackVM)

**Stack:** Spring Boot 4.0.6, Java 17, JPA/Hibernate, Flyway, PostgreSQL, Lombok

**Pacote raiz:** `com.felp.backvm`

**Endpoints REST:**

| Método | Rota | Ação |
|--------|------|------|
| POST | `/api/users/register` | Cadastro (cria com plano BASIC) |
| POST | `/api/users/login` | Login |
| PATCH | `/api/users/{id}/plan` | Mudar plano do usuário |
| GET | `/api/vms` | Listar VMs do usuário (header `X-User-Id`) |
| POST | `/api/vms` | Criar VM (valida plano: limite de quantidade e perfis) |
| PATCH | `/api/vms/{id}/start` | Ligar VM |
| PATCH | `/api/vms/{id}/pause` | Pausar VM |
| PATCH | `/api/vms/{id}/stop` | Desligar VM |
| DELETE | `/api/vms/{id}` | Deletar VM |

Todos os endpoints de `/api/vms` exigem o header `X-User-Id` com o id do usuário logado. O service filtra por `userId`, então não há vazamento entre contas.

**Erros:**
- 401 Unauthorized — credenciais inválidas
- 404 Not Found — VM/usuário não encontrado
- 409 Conflict — e-mail já cadastrado
- 422 Unprocessable Entity — transição de estado inválida ou limite de plano excedido

**Estrutura de pacotes:**
- `controller/` — VirtualMachineController, UserController
- `service/` — VirtualMachineService, UserService, **DockerService** (orquestra containers via CLI do Docker)
- `domain/` — VirtualMachine, User; enums: VmStatus, VmProfile, OsType, UserPlan
- `dto/` — CreateVmRequest, VirtualMachineDTO, RegisterRequest, LoginRequest, UserDTO, ChangePlanRequest
- `repository/` — VirtualMachineRepository, UserRepository
- `exception/` — GlobalExceptionHandler, VmNotFoundException, UserNotFoundException, InvalidCredentialsException, EmailAlreadyExistsException, PlanLimitException
- `util/` — PasswordHasher (SHA-256 com salt aleatório por usuário, formato `salt:hash` em base64)

**Integração Docker:**
- `DockerService` executa `docker` via `ProcessBuilder` (sem shell, sem injection)
- VMs Ubuntu viram containers `simplevm-{vmId}` baseados na imagem `ubuntu` rodando `sleep infinity`
- Mapeamento: criar→`docker create`, iniciar→`docker start` (com `unpause` se pausado), pausar→`docker pause`, parar→`docker stop`, deletar→`docker rm -f`
- VMs Windows 11 não viram containers (continuam só estado no banco)
- Erros do Docker são logados como warning mas não falham a operação no banco (best effort)
- Pré-requisito: Docker Desktop rodando + `docker pull ubuntu` feito ao menos uma vez

**Banco de dados:**
- PostgreSQL na porta 5433
- DB: `vmmanager`, User: `vmuser`, Pass: `vmpass`
- Schema gerenciado por Flyway:
  - `V1__create_virtual_machines.sql`
  - `V2__rename_windows_10_to_ubuntu.sql`
  - `V3__create_users_and_link_vms.sql` — cria tabela `users`, limpa `virtual_machines`, adiciona `user_id NOT NULL` com FK

## Frontend (FrontVM)

**Stack:** JavaFX 21, Java 21, ControlsFX, org.json, Java HttpClient

**Pacote raiz:** `com.felp.frontvm`

**Telas (FXMLs em `src/main/resources/com/felp/frontvm/`):**
- **LoginController** + `login-view.fxml` — Login/Cadastro com toggle (campos extras de nome e confirmação só aparecem no modo cadastro)
- **PlansController** + `plans-view.fxml` — 3 cards de plano, badge "Plano atual", botão `Comprar` em Standard/Premium e `Voltar ao Basic` para downgrade. Modal de confirmação antes de trocar de plano. Header com "Olá, {nome}", "Plano atual: {plano}", botões "Minhas VMs" e "Sair"
- **MainController** + `main-view.fxml` — Grade de VMs do usuário; header mostra nome, plano, contador `X/Y`. Botão "Nova VM" desabilita ao atingir o limite. Botões "Meu plano" e "Sair" no header
- **CriarVmController** + `criar-vm-view.fxml` — Modal: nome, OS (Ubuntu/Windows 11), perfil. Erros do backend (limite de plano, perfil não permitido) viram alertas
- **VmViewerController** + `vm-viewer-view.fxml` — Visualizador com wallpaper real centralizado quando RUNNING. Botão "Abrir terminal" só aparece em VMs Ubuntu RUNNING; ele invoca `cmd.exe /c start docker exec -it simplevm-{id} bash -c "<script de stats>; exec bash"` para abrir uma janela externa do Windows com shell interativo

**Sessão e navegação:**
- `session/Session.java` — singleton estático que guarda o `UserModel` logado em memória
- `session/Navigator.java` — `Navigator.to(stage, fxml, w, h, title)` troca a scene da Stage e ajusta tamanho com `sizeToScene()`
- `service/VmApiService.java` — adiciona automaticamente o header `X-User-Id` lendo de `Session.getUserId()`
- `service/UserApiService.java` — login, register, changePlan; lança `ApiException(status, msg)` em erros HTTP

**Padrões de UI:**
- CSS inline
- Ícones SVG via `LucideIcons.java` (lucide.dev)
- Operações assíncronas com `new Thread(...).start()` + `Platform.runLater()`
- Cards coloridos: Ubuntu = laranja, Windows 11 = roxo
- Status: verde (RUNNING), âmbar (PAUSED), cinza (STOPPED)

**Configuração:** `ApiConfig.java` define `BASE_URL = "http://localhost:8080"`

## Domínio

**UserPlan:**
- `BASIC` (grátis) — até 1 VM, perfil WEAK apenas
- `STANDARD` (R$ 19,90/mês) — até 3 VMs, perfis WEAK e MEDIUM
- `PREMIUM` (R$ 49,90/mês) — VMs ilimitadas, todos os perfis

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
- **Sessão 7** — Cadastro/login + planos (Basic/Standard/Premium), confirmação fictícia de compra, filtragem de VMs por usuário
- **Sessão 8** — Integração com Docker: VMs Ubuntu rodam como containers reais (`simplevm-{id}`), botão "Abrir terminal" no viewer abre PowerShell externo com `docker exec -it ... bash`. README com passo-a-passo de validação fora do app

## Convenções

- Mensagens de erro e labels de UI em português
- Não rodar comandos no terminal — apenas entregar código; o usuário executa mvn/docker/git
- Sem comentários desnecessários no código
- Lombok para reduzir boilerplate (@Data, @Builder, @RequiredArgsConstructor)
- Sem Spring Security: hash de senha próprio com SHA-256 + salt aleatório (`util/PasswordHasher.java`)

# SimpleVM

Aplicação acadêmica que simula um painel de gerenciamento de máquinas virtuais. As VMs do tipo **Ubuntu** são executadas como containers Docker reais (Linux de verdade rodando), enquanto as VMs do tipo **Windows 11** são apenas simulação visual.

## Stack

- **Backend** (`BackVM/`): Spring Boot 4.0.6, JPA, Flyway, Java 17
- **Frontend** (`FrontVM/`): JavaFX 21
- **Banco**: PostgreSQL 15 (via Docker)
- **VMs Ubuntu**: containers Docker baseados na imagem `ubuntu`

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
- Docker Desktop (precisa estar rodando, com WSL2 como backend)
- Imagem Ubuntu baixada localmente:

```bash
docker pull ubuntu
```

Faça isso uma vez antes de criar a primeira VM Ubuntu no app — assim a criação fica instantânea.

## Como rodar

**1. Subir o banco**

```bash
docker compose up -d
```

Postgres fica exposto em `localhost:5433`.

**2. Rodar o backend**

```bash
cd BackVM
mvn spring-boot:run
```

API disponível em `http://localhost:8080`. As migrations do Flyway criam as tabelas `users` e `virtual_machines` na primeira execução.

**3. Rodar o frontend**

Em outro terminal:

```bash
cd FrontVM
mvn clean javafx:run
```

A janela do SimpleVM abre na tela de login.

## Endpoints principais

### Usuários

| Método | Rota                   | Ação                                      |
|--------|------------------------|-------------------------------------------|
| POST   | `/api/users/register`  | Cadastro (cria com plano BASIC)           |
| POST   | `/api/users/login`     | Login                                     |
| PATCH  | `/api/users/{id}/plan` | Mudar plano                               |

### VMs (todos exigem header `X-User-Id`)

| Método | Rota                  | Ação                          |
|--------|-----------------------|-------------------------------|
| GET    | `/api/vms`            | Listar VMs do usuário         |
| POST   | `/api/vms`            | Criar VM                      |
| PATCH  | `/api/vms/{id}/start` | Ligar                         |
| PATCH  | `/api/vms/{id}/pause` | Pausar                        |
| PATCH  | `/api/vms/{id}/stop`  | Desligar                      |
| DELETE | `/api/vms/{id}`       | Remover                       |

## Funcionalidades

- Cadastro/login de usuários (senha com hash SHA-256 + salt)
- Planos: Basic (1 VM, perfil Fraco), Standard (3 VMs, Fraco+Médio), Premium (ilimitadas, todos os perfis)
- Cada usuário só vê as próprias VMs
- Criar VM com nome, sistema (Ubuntu/Windows 11) e perfil de hardware
- Ligar, pausar, parar e excluir
- VMs Ubuntu rodam em containers Docker reais (kernel Linux de verdade)
- Tela cheia da VM com wallpaper centralizado
- Botão "Abrir terminal" (apenas para VMs Ubuntu em execução) abre uma janela do Windows com bash interativo dentro do container

## Provando que tem um Linux real rodando

Quando você cria uma VM Ubuntu no app, o backend cria um container Docker chamado `simplevm-{id}`. As ações no app mapeiam direto para o Docker:

| Ação no app   | Comando Docker                       |
|---------------|--------------------------------------|
| Criar VM      | `docker create --name simplevm-{id} ubuntu sleep infinity` |
| Iniciar       | `docker start simplevm-{id}`         |
| Pausar        | `docker pause simplevm-{id}`         |
| Parar         | `docker stop simplevm-{id}`          |
| Deletar       | `docker rm -f simplevm-{id}`         |

### Passo a passo para validar fora do app

Após criar e iniciar uma VM Ubuntu no app (suponha que ela tenha id `1`), abra um PowerShell e execute:

**1. Listar containers ativos:**

```bash
docker ps
```

Deve aparecer `simplevm-1` com status `Up X seconds`.

**2. Confirmar que é Linux:**

```bash
docker exec simplevm-1 uname -a
```

Saída esperada: algo como `Linux <hash> 5.15.... x86_64 GNU/Linux`.

**3. Confirmar a distribuição Ubuntu:**

```bash
docker exec simplevm-1 cat /etc/os-release
```

Saída esperada: `NAME="Ubuntu"`, `VERSION="..."`, etc.

**4. Estatísticas de CPU/memória em tempo real:**

```bash
docker stats --no-stream simplevm-1
```

**5. Abrir um shell interativo (mesmo que o botão do app):**

```bash
docker exec -it simplevm-1 bash
```

Dentro do shell, você pode:

```bash
hostname            # nome do container
ls /                # raiz do filesystem Linux
cat /proc/cpuinfo   # detalhes da CPU
apt list --installed 2>/dev/null | head   # pacotes instalados
exit                # sair do container
```

**6. Pelo botão do app:**

Abra a VM no SimpleVM (clique no card), com a VM em execução clique em **"Abrir terminal"** no canto superior direito. Uma nova janela do Windows abre já mostrando `uname`, `/etc/os-release`, `meminfo`, `df` e em seguida um bash interativo.

### Troubleshooting

- **"docker: command not found"** → Docker Desktop não está instalado ou não está no PATH.
- **"Cannot connect to the Docker daemon"** → Docker Desktop não está rodando. Inicie o Docker Desktop e tente de novo.
- **VM Ubuntu fica em estado inconsistente** → no PowerShell rode `docker rm -f simplevm-{id}` e delete a VM no app; depois recrie.
- **Botão "Abrir terminal" não aparece** → só aparece para VMs Ubuntu em estado RUNNING.

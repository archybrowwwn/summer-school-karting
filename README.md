# Summer School — картинг «Апекс»

Учебный проект: клиентское мобильное приложение для записи на заезды в картинг-центре «Апекс».

## Сдача ДЗ (для проверяющего)

**Журнал выполнения задания летней школы (п.1–4):** [03-homework/README.md](03-homework/README.md)

Там же — пошаговая **проверка за 10 минут** (запуск + что кликнуть в UI).

## Структура

| Папка | Содержимое |
|-------|------------|
| `01-analysis/` | Аналитика и ТЗ по проекту «Апекс» (бриф, требования, экраны, OpenAPI) |
| `02-development/` | Планы реализации backend и CMP-клиента |
| `03-homework/` | Журнал домашнего задания летней школы (п.1–4) |
| `backend/` | Go API + PostgreSQL для картинг-центра «Апекс» |
| `client/` | Kotlin Compose Multiplatform — Android, Web, iOS |

## Быстрый старт

Подробные команды — в [LOCAL_DEV_GUIDE.md](LOCAL_DEV_GUIDE.md). Сценарий проверки ДЗ — [03-homework/README.md](03-homework/README.md#проверка-за-10-минут-для-преподавателя).

### Linux / macOS / Git Bash

```bash
# Терминал 1 — backend
cd backend
docker compose --profile db up -d db
make migrate
docker compose -f compose.yaml cp seed/demonstration_base.sql db:/tmp/demonstration_base.sql
docker compose -f compose.yaml exec -T db psql -U apex -d apex -f /tmp/demonstration_base.sql
make run

# Терминал 2 — Web-клиент
cd client
./gradlew :webApp:wasmJsBrowserDevelopmentRun
```

### Windows PowerShell (без `make`)

**Терминал 1** — `backend/`:

```powershell
docker compose --profile db up -d db
$env:DATABASE_URL = "postgres://apex:apex@localhost:5433/apex?sslmode=disable"
go run github.com/pressly/goose/v3/cmd/goose@v3.24.1 -dir migrations postgres $env:DATABASE_URL up
docker compose -f compose.yaml cp seed/demonstration_base.sql db:/tmp/demonstration_base.sql
docker compose -f compose.yaml exec -T db psql -U apex -d apex -f /tmp/demonstration_base.sql
go run ./cmd/api
```

Проверка API (терминал не закрывать):

```powershell
curl.exe http://127.0.0.1:8080/healthz
```

Ожидается ответ `ok`.

**Терминал 2** — `client/`:

```powershell
.\gradlew.bat :webApp:wasmJsBrowserDevelopmentRun
```

Браузер: **`http://localhost:8081`** → вход `+79990000001` → код на экране («Код для разработки»).

### После `healthz` → `ok`

1. Терминал с `go run ./cmd/api` **оставить запущенным**.
2. Во **втором** терминале запустить Web (см. выше).
3. Открыть **`http://localhost:8081`** (не `[::1]:8081`).
4. Войти как `+79990000001`, проверить «Заезды» → бронь → «Мои записи».

### Частые проблемы на Windows

| Симптом | Решение |
|---------|---------|
| `make` не распознан | Используйте блок PowerShell выше или [LOCAL_DEV_GUIDE.md §1.1](LOCAL_DEV_GUIDE.md#11-windows-powershell-без-make) |
| `curl` спрашивает про «риск сценария» | В PowerShell `curl` — это не curl, а `Invoke-WebRequest`. Используйте **`curl.exe`** или `Invoke-RestMethod http://127.0.0.1:8080/healthz` |
| Мало слотов / пустой UI | Примените `demonstration_base.sql` (включён в сценарии выше) |
| «Не удалось войти» | API запущен? Открыт `localhost:8081`, не IPv6-ссылка из webpack |
| Первая сборка Web «висит» | Нормально 5–15 мин при первом `gradlew.bat` |

## Репозиторий

https://github.com/archybrowwwn/summer-school-karting
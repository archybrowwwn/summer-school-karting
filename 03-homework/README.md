# Домашнее задание — летняя школа · картинг «Апекс»

Журнал выполнения задания по брифу картинг-центра. Каждый пункт содержит цель, ссылки на требования, промпты, ручную проверку и коммиты.

## Проверка за 10 минут (для преподавателя)

**Что нужно:** Docker, Go 1.23+, JDK 17+. Android/Xcode не обязательны — проверяйте через **Web**.

<details>
<summary><b>Windows PowerShell (без make) — рекомендуется на Windows</b></summary>

**Терминал 1** — из корня репозитория или после `git clone`:

```powershell
cd backend
docker compose --profile db up -d db
$env:DATABASE_URL = "postgres://apex:apex@localhost:5433/apex?sslmode=disable"
go run github.com/pressly/goose/v3/cmd/goose@v3.24.1 -dir migrations postgres $env:DATABASE_URL up
docker compose -f compose.yaml cp seed/demonstration_base.sql db:/tmp/demonstration_base.sql
docker compose -f compose.yaml exec -T db psql -U apex -d apex -f /tmp/demonstration_base.sql
go run ./cmd/api
```

**Терминал 2:**

```powershell
cd client
.\gradlew.bat :webApp:wasmJsBrowserDevelopmentRun
```

Браузер: **`http://localhost:8081`**. Подробнее: [LOCAL_DEV_GUIDE.md §1.1](../LOCAL_DEV_GUIDE.md#11-windows-powershell-без-make).

</details>

### 1. Клонировать и поднять БД

```bash
git clone https://github.com/archybrowwwn/summer-school-karting.git
cd summer-school-karting/backend
docker compose --profile db up -d db
make migrate
```

На **Windows без `make`**: см. блок PowerShell выше или [LOCAL_DEV_GUIDE.md §1.1](../LOCAL_DEV_GUIDE.md#11-windows-powershell-без-make).

### 2. Демо-данные (обязательно для UI)

Без этого шага в клиенте будет мало слотов и броней. Seed пересчитывает даты относительно **сегодня**.

```bash
docker compose -f compose.yaml cp seed/demonstration_base.sql db:/tmp/demonstration_base.sql
docker compose -f compose.yaml exec -T db psql -U apex -d apex -f /tmp/demonstration_base.sql
```

> Не загружайте SQL через PowerShell `Get-Content` без `-Encoding UTF8` — кириллица станет `??????`.

### 3. Запустить API

```bash
make run
```

PowerShell: `$env:DATABASE_URL = "postgres://apex:apex@localhost:5433/apex?sslmode=disable"; go run ./cmd/api`

Проверка (PowerShell: **`curl.exe`**, не `curl` — иначе предупреждение Invoke-WebRequest):

```powershell
curl.exe http://127.0.0.1:8080/healthz
```

Ожидается `ok`. Терминал с API **не закрывать** — переходите к шагу 4.

### 4. Запустить Web-клиент

В **втором** терминале:

```bash
cd ../client
./gradlew :webApp:wasmJsBrowserDevelopmentRun
```

PowerShell: `.\gradlew.bat :webApp:wasmJsBrowserDevelopmentRun`

Первая сборка Wasm может занять **5–15 минут** — это нормально.

Откройте в браузере: **`http://localhost:8081`** (не `[::1]:8081`).

### 5. Что проверить в UI (3 фичи п.3)

| # | Фича | Шаги | Ожидание |
|---|------|------|----------|
| 1 | **OTP-вход** | Телефон `+79990000001` → «Получить код» → код на экране («Код для разработки») → подтвердить | Список заездов |
| 2 | **Слоты + бронь** | Вкладка «Заезды» → фильтры (уровень/маршрут) → тап по слоту → «Записаться» → 1–2 места → подтвердить | Экран успеха, бронь в «Мои записи» |
| 3 | **Отмена** | «Мои записи» → тап по брони → «Отменить» → подтвердить | Статус «Отменена» или «Поздняя отмена» |

Подробные тест-кейсы и баг-репорты: [task-04-test-cases-bugs.md](task-04-test-cases-bugs.md).

### 6. Автотесты (опционально)

```bash
cd backend && go test ./... -count=1
cd client && ./gradlew :shared:wasmJsTest
```

PowerShell: `.\gradlew.bat :shared:wasmJsTest`

Полный гайд: [LOCAL_DEV_GUIDE.md](../LOCAL_DEV_GUIDE.md) (в т.ч. [§1.1 PowerShell](../LOCAL_DEV_GUIDE.md#11-windows-powershell-без-make)).

---

## Статус

| Пункт ДЗ | Журнал | Статус |
|----------|--------|--------|
| **1.** MVP-требования (user stories + сценарии) | [task-01-mvp-requirements.md](task-01-mvp-requirements.md) | ✅ Оформлено |
| **2.** Архитектура + модель данных | [task-02-architecture-data-model.md](task-02-architecture-data-model.md) | ✅ Оформлено |
| **3.** Реализация ≥3 фич | см. ниже | ✅ Оформлено |
| **4.** Тест-кейсы + 1–3 бага | [task-04-test-cases-bugs.md](task-04-test-cases-bugs.md) | ✅ Оформлено |

### Фичи (п.3)

| Фича | Журнал | SCR / LOGIC |
|------|--------|-------------|
| OTP-авторизация | [task-03-feature-auth.md](task-03-feature-auth.md) | SCR-001, LOGIC-001 |
| Слоты и бронирование | [task-03-feature-slots-booking.md](task-03-feature-slots-booking.md) | SCR-002–004, BS-001–002, LOGIC-002–003 |
| Мои брони и отмена | [task-03-feature-bookings-cancel.md](task-03-feature-bookings-cancel.md) | SCR-005–006, BS-003, LOGIC-004 |

## Связанные артефакты

- Аналитика: [`01-analysis/`](../01-analysis/)
- Планы разработки: [`02-development/`](../02-development/)
- Backend: [`backend/`](../backend/)
- Клиент: [`client/`](../client/)
- Промпты (лекция): [`01-analysis/prompts/`](../01-analysis/prompts/)

## Коммиты журнала и реализации

| Коммит | Содержание |
|--------|------------|
| `c6c8e03` | docs: homework p.1 MVP requirements journal and bad-prompts |
| `2ebf47c` | docs: homework p.2 architecture journal and status sync |
| `aba49ae` | docs: homework p.3 three feature journals with manual verification |
| `7818462` | docs: homework p.4 test cases and bug reports |
| `873a8b6` | feat: demonstration database, CORS and web client cyrillic |
| `8438d96` | feat(client-ui): slot filters, unified icons and layout polish |
| `54fc6b0` | chore: cleanup dead code and align docs with Apex project |
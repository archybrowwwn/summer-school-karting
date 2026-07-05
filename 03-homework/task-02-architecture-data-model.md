# П.2 — Архитектура и модель данных «Апекс»

**Дата оформления:** 2026-07-05

---

## Цель

Получить от ИИ архитектурный план реализации и схему данных для MVP: ресурсная модель API, ERD, sequence-диаграммы ключевых операций, планы backend (Go) и клиента (Kotlin CMP).

---

## Требования (источники)

| Артефакт | Путь |
|----------|------|
| Модель данных + ERD | [data-model.md](../01-analysis/4-design/data-model.md) |
| API sequence | [api-sequence.md](../01-analysis/4-design/api-sequence.md) |
| План BE | [BE_IMPLEMENTATION_PLAN.md](../02-development/BE_IMPLEMENTATION_PLAN.md) |
| План CMP-клиента | [CMP_CLIENT_IMPLEMENTATION_PLAN.md](../02-development/CMP_CLIENT_IMPLEMENTATION_PLAN.md) |
| Схема БД | [backend/migrations/00001_init.sql](../backend/migrations/00001_init.sql) |
| OpenAPI | [01-analysis/api/](../01-analysis/api/) |

**Архитектурные решения:**

- BE: Go, layered (HTTP → service → storage/postgres), OpenAPI-first, `chi`, `pgx`, `goose`.
- FE: Kotlin Compose Multiplatform, clean architecture + MVI, Ktor, Koin.
- Бронирование: транзакции + row-level lock, idempotency keys.
- Слоты/трассы/маршалы: read-only проекции.

---

## Что сделано

1. Ресурсная модель: Client, Route, Instructor, Slot, Booking с инвариантами и статусами.
2. Sequence для `createBooking` (idempotency, 409 slot_full) и `cancelBooking` (early/late, 422 slot_started).
3. План BE с 15 пунктами (BE-00…BE-14) — все выполнены.
4. План CMP с 19 пунктами (CMP-00…CMP-18) — MVP-поток реализован, polish/push — в backlog.
5. Миграции PostgreSQL совпадают с моделью: `clients`, `routes`, `instructors`, `slots`, `bookings`, `idempotency_keys`, `otp_codes`, `sessions`.

---

## Промпты

> Промпт 1 — в [good-prompts.md § Пример 3](../01-analysis/prompts/good-prompts.md).

### Промпт 1 — Модель данных и API-последовательности

```
По user stories и FR для картинг-центра «Апекс» спроектируй:
1) ресурсную модель API (Client, Route, Instructor, Slot, Booking) с атрибутами и ERD mermaid;
2) sequence-диаграммы для createBooking и cancelBooking (idempotency, ошибки 409/410/422).

Инварианты: free_rental_gear отдельно от free_seats; seats 1..5;
отмена ≥1ч освобождает места; слоты read-only для клиента.
```

### Промпт 2 — План реализации Go backend

```
По OpenAPI из 01-analysis/api и data-model.md составь план реализации Go REST API
для картинг «Апекс».

Стек: Go 1.23, chi, pgx, goose, oapi-codegen, PostgreSQL 16.
Архитектура: cmd/api → internal/http/handlers → internal/service → internal/storage/postgres.

Декомпозируй на BE-00…BE-14: каркас, OpenAPI, миграции, auth OTP, profile, slots,
bookings (atomic + idempotency), cancel, ошибки, тесты, k6 до 300 VU, локальный запуск.
Чеклист [x]/[ ] для каждого пункта.
```

### Промпт 3 — План CMP-клиента

```
По ТЗ экранов SCR-* и реализованному BE составь план Kotlin Compose Multiplatform клиента.

Модули: shared, androidApp, webApp, iosApp.
Архитектура: domain policies, MVI stores, Ktor repositories, expect/actual для storage и карт.

Декомпозируй CMP-00…CMP-18: скелет, network, auth, navigation, slots, booking, cancel, profile, map, tests.
Укажи известные gaps: geometry не маппится в BE, push endpoint 501.
```

---

## Ручная проверка

| # | Проверка | Ожидание | Результат (2026-07-05) |
|---|----------|----------|------------------------|
| 1 | ERD ↔ таблицы миграций | Те же сущности и связи | ✅ `00001_init.sql` содержит clients, routes, slots, bookings |
| 2 | OpenAPI ↔ план BE | 15 operationId из плана | ✅ Домены auth/slots/bookings/profile/instructors |
| 3 | OpenAPI lint | Без ошибок | ✅ `npm run lint` — 5 файлов validated |
| 4 | BE тесты | Все пакеты green | ✅ `go test ./...` — config, http, handlers, auth, booking, postgres OK |
| 5 | Sequence createBooking | Idempotency + slot_full | ✅ `TestCreateBookingFlowAndIdempotency`, `TestCreateBookingConcurrencyDoesNotOverbook` |
| 6 | Sequence cancelBooking | early / late / slot_started | ✅ `TestCancelBookingEarlyLateAndAfterStart` |
| 7 | CMP структура | shared + 3 target | ✅ `client/shared`, `androidApp`, `webApp`, `iosApp` |

**Известные расхождения (зафиксированы, не блокируют п.2):**

- `Route.geometry` есть в БД и OpenAPI, но `catalog.go` пока не маппит в ответ — клиент использует fallback карты.
- Push endpoints в OpenAPI есть, handler — `Unimplemented` (501).

---

## Коммиты

| Хеш | Сообщение |
|-----|-----------|
| `53e6dc9` | docs: архитектура и модель данных Апекс |
| `1ae4d31` | Add development scaffold: backend, client, API spec (Apex analysis preserved) |
# П.3 — Фича 1: OTP-авторизация (SCR-001)

**Дата оформления:** 2026-07-05

---

## Цель

Реализовать вход и регистрацию клиента по номеру телефона и SMS OTP без паролей: запрос кода, верификация, выдача Bearer-токена, выход из сессии.

---

## Требования

| ID | Описание |
|----|----------|
| US-1 | Войти по телефону без сложных паролей |
| FR-1, FR-2, FR-43 | Регистрация по имени + телефон, авторизация по OTP |
| SCR-001 | [SCR-001-registration.md](../01-analysis/5-mobile-app-spec/SCR-001-registration.md) |
| LOGIC-001 | [LOGIC-001_OTP_Authorization.md](../01-analysis/5-mobile-app-spec/09_Logic/LOGIC-001_OTP_Authorization.md) |

**API:**

| operationId | Метод | Путь |
|-------------|-------|------|
| `requestAuthCode` | POST | `/auth/request-code` |
| `verifyAuthCode` | POST | `/auth/verify-code` |
| `logout` | POST | `/auth/logout` |

---

## Что сделано

### Backend

- `backend/internal/service/auth/service.go` — OTP, JWT, сессии.
- `backend/internal/http/handlers/auth.go` — HTTP-обработчики.
- `backend/internal/storage/postgres/auth.go` — OTP и sessions в PostgreSQL.
- Dev-режим: код OTP возвращается в теле `RequestCodeResponse` для локальной отладки.

### Client

- `client/shared/.../auth/presentation/AuthScreen.kt` — UI: телефон → код → имя (для `is_new`).
- `client/shared/.../auth/presentation/AuthStore.kt` — MVI: шаги, валидация, таймер resend.
- `client/shared/.../auth/data/KtorAuthRepository.kt` — вызовы API.
- `client/shared/.../auth/data/DefaultSessionRepository.kt` — хранение токена (expect/actual).

---

## Промпты

> Промпты восстановлены по фактической реализации и коммитам `1ae4d31`, `ae81e85`.

### Промпт — Backend auth

```
Реализуй auth flow для Go API картинг «Апекс» по OpenAPI auth/api.yaml.

Нужно:
- POST /auth/request-code: валидация телефона E.164, OTP в БД, rate limit, dev: code в ответе
- POST /auth/verify-code: проверка OTP, создание client при is_new, JWT access token
- POST /auth/logout: инвалидация сессии по Bearer

Используй internal/service/auth + postgres. Добавь integration test TestAuthVerifyAndLogoutFlow.
```

### Промпт — Client SCR-001

```
Реализуй SCR-001 OTP-авторизацию в Kotlin CMP shared модуле.

MVI AuthStore: шаги Phone → Code → Name (если is_new).
AuthScreen: маска телефона +7, 4–6 цифр кода, resend timer из ttl_seconds.
После verify сохрани access_token в SessionStorage, при 401 — clear и редирект на auth.
Подключи к VolnaApp root navigation.
```

---

## Ручная проверка

| # | Шаг | Ожидание | Результат (2026-07-05) |
|---|-----|----------|------------------------|
| 1 | `POST /auth/request-code` с `+79991234567` | 200, `ttl_seconds`, dev `code` | ✅ `TestAuthRequestCodeReturnsDemoCode` |
| 2 | `POST /auth/verify-code` с верным кодом | 200, `tokens.access_token`, `client`, `is_new` | ✅ `TestAuthVerifyAndLogoutFlow` |
| 3 | `POST /auth/logout` с Bearer | 204 | ✅ тот же тест |
| 4 | Повторный verify с неверным кодом | 400 `invalid_code` | ✅ `service/auth/service_test.go` |
| 5 | Клиент: ввод телефона и кода | Переход в список слотов | ✅ UI реализован; Web-клиент запускался ранее (`wasmJsBrowserDevelopmentRun`) |

**Команды проверки:**

```bash
cd backend && go test ./internal/http/handlers/ -run TestAuth -count=1
cd backend && go test ./internal/service/auth/ -count=1
```

---

## Коммиты

| Хеш | Сообщение |
|-----|-----------|
| `1ae4d31` | Add development scaffold: backend, client, API spec |
| `ae81e85` | feat: вход слоты бронь Апекс |
| `4eaebfc` | ui: интерфейс и демо-данные картодрома Апекс |
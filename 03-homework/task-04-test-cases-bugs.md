# П.4 — Тест-кейсы и баги (MVP «Апекс»)

**Дата оформления:** 2026-07-05

---

## Цель

Систематически проверить три реализованные фичи из п.3 (OTP-вход, слоты/бронь, мои брони/отмена), зафиксировать набор тест-кейсов и описать 1–3 найденных дефекта с шагами воспроизведения, причиной и исправлением.

---

## Требования (источники)

| Артефакт | Путь |
|----------|------|
| User stories US-1…US-10 | [user-stories.md](../01-analysis/2-requirements/user-stories.md) |
| Use cases UC-1…UC-4 | [use-cases.md](../01-analysis/2-requirements/use-cases.md) |
| Журналы фич п.3 | [task-03-feature-auth.md](task-03-feature-auth.md), [task-03-feature-slots-booking.md](task-03-feature-slots-booking.md), [task-03-feature-bookings-cancel.md](task-03-feature-bookings-cancel.md) |
| Демо-данные | [demonstration_base.sql](../backend/seed/demonstration_base.sql) |
| Локальный запуск | [LOCAL_DEV_GUIDE.md](../LOCAL_DEV_GUIDE.md) |

**Охват проверки:**

- Ручные сценарии в Web-клиенте (`http://localhost:8081`) + API через backend.
- Автотесты Go (integration) и Kotlin (`commonTest` / `wasmJsTest`) как подтверждение инвариантов.

---

## Набор тест-кейсов

### Auth — SCR-001 / LOGIC-001

| ID | Сценарий | Предусловия | Шаги | Ожидание |
|----|----------|-------------|------|----------|
| TC-A1 | Вход существующего клиента | BE + DB + seed; Web на `localhost:8081` | 1. Телефон `9990000001` → «Получить код» 2. Ввести OTP из ответа API / лога BE 3. Подтвердить | Переход в приложение, список слотов доступен |
| TC-A2 | Регистрация нового клиента | Номер не в seed, напр. `+79991112233` | 1. Запросить код 2. Ввести OTP 3. Ввести имя | Шаг «Имя», затем вход, профиль с указанным именем |
| TC-A3 | Неверный OTP | Код запрошен для известного номера | 1. Ввести `0000` вместо реального кода | Сообщение «Код неверен или просрочен», остаёмся на шаге OTP |
| TC-A4 | Rate limit повторной отправки | Код запрошен < 60 с назад | 1. Сразу снова «Получить код» | 429 / сообщение о таймере, `resend_after_seconds` |

### Слоты и бронирование — SCR-002…004 / LOGIC-002…003

| ID | Сценарий | Предусловия | Шаги | Ожидание |
|----|----------|-------------|------|----------|
| TC-S1 | Список заездов на неделю | Seed `demonstration_base.sql` применён | 1. Войти как `+79990000001` 2. Открыть вкладку слотов | ≥ 30 слотов на следующую неделю, даты и маршалы читаемы |
| TC-S2 | Фильтр по типу трассы | Список слотов загружен | 1. Открыть фильтры (BS-001) 2. Выбрать «новичковая» | Только слоты novice-маршрутов |
| TC-S3 | Карточка слота | Есть слот со свободными местами | 1. Тап по слоту | SCR-003: время, трасса, маршал, адрес встречи, свободные места и прокат |
| TC-S4 | Успешная бронь | Слот с `free_seats ≥ 2` | 1. «Записаться» 2. 2 места, 1 прокат 3. Подтвердить | BS-002 success, бронь в «Мои записи», `price_total` с сервера |
| TC-S5 | Слот без мест | Слот с `free_seats = 0` (пн 15:00 в seed) | 1. Открыть карточку | «Мест нет», CTA брони недоступен |

### Мои брони и отмена — SCR-005…006 / LOGIC-004

| ID | Сценарий | Предусловия | Шаги | Ожидание |
|----|----------|-------------|------|----------|
| TC-B1 | Список броней | У `+79990000001` есть брони в seed | 1. Вкладка «Мои записи» | Секции предстоящие/прошедшие, карточки с трассой и временем |
| TC-B2 | Детали брони | Есть активная бронь | 1. Тап по брони | SCR-006: места, прокат, `price_total`, статус, слот |
| TC-B3 | Ранняя отмена | Бронь на слот ≥ 1 ч в будущем | 1. «Отменить» → подтвердить (BS-003) | `status=cancelled`, места возвращены в слот |
| TC-B4 | Поздняя отмена | Бронь < 1 ч до старта (проверка через API/тест) | 1. `POST /bookings/{id}/cancel` в окне < 1 ч | `status=late_cancel`, `free_seats` слота не растут |

---

## Найденные баги

### BUG-001 — Вход не работает при origin `http://[::1]:8081` (CORS)

| Поле | Значение |
|------|----------|
| **Серьёзность** | High (блокер входа) |
| **Область** | Backend CORS + Web-клиент |
| **Шаги** | 1. Запустить BE и Web 2. Открыть сайт по ссылке webpack `http://[::1]:8081/` 3. Ввести `+79990000001` → «Получить код» |
| **Ожидание** | 200 на `POST /auth/request-code`, переход к OTP |
| **Факт** | Запрос заблокирован браузером (CORS); UI: «Не удалось войти» |
| **Причина** | `corsMiddleware` разрешал только `localhost:8081` и `127.0.0.1:8081`, без IPv6 loopback |
| **Исправление** | `isAllowedDevOrigin()` для `localhost`, `127.0.0.1`, `::1`; нормализация API host в `ApiBaseUrl.wasm.kt` |
| **Файлы** | `backend/internal/http/middleware.go`, `client/shared/.../ApiBaseUrl.wasm.kt` |
| **Коммит** | `873a8b6` |

### BUG-002 — Кириллица в БД превращается в `??????` при загрузке seed

| Поле | Значение |
|------|----------|
| **Серьёзность** | High (демо непригодно для показа) |
| **Область** | Seed / PostgreSQL / Windows |
| **Шаги** | 1. `Get-Content seed/....sql \| docker compose exec ... psql` (без `-Encoding UTF8`) 2. Открыть список слотов |
| **Ожидание** | Имена маршалов «Мария», адреса «Павильон у стартовой прямой» |
| **Факт** | `??????` в UI и в `SELECT name FROM instructors` |
| **Причина** | PowerShell по умолчанию читает файл не в UTF-8; psql получает битую кодировку |
| **Исправление** | Seed `demonstration_base.sql`; загрузка через `docker compose cp` + `psql -f`; предупреждение в `LOCAL_DEV_GUIDE.md` |
| **Файлы** | `backend/seed/demonstration_base.sql`, `LOCAL_DEV_GUIDE.md` |
| **Коммит** | `873a8b6` |

### BUG-003 — Кириллица не отображается в Web-клиенте (шрифт Wasm)

| Поле | Значение |
|------|----------|
| **Серьёзность** | Medium (данные в API корректны, UI нечитаем) |
| **Область** | CMP Web / Skiko |
| **Шаги** | 1. Исправить BUG-002 2. Открыть слот в Web без кастомного шрифта |
| **Ожидание** | Русские названия трасс и маршалов |
| **Факт** | Квадраты / отсутствие глифов при дефолтной типографике Material на Wasm |
| **Причина** | Не был подключён шрифт с поддержкой кириллицы |
| **Исправление** | Inter Regular/Bold в `composeResources/font/`, `ApexFonts.kt`, `apexTypography()` |
| **Файлы** | `client/shared/src/commonMain/composeResources/font/`, `ApexFonts.kt`, `ApexTheme.kt`, `build.gradle.kts` |
| **Коммит** | `873a8b6` |

---

## Промпты

### Промпт 1 — Матрица тест-кейсов

```
По трём фичам MVP «Апекс» (auth, slots/booking, bookings/cancel) составь таблицу
ручных тест-кейсов: ID, предусловия, шаги, ожидание. Покрой SCR-001…006,
LOGIC-001…004, негативные сценарии (invalid OTP, slot_full, late_cancel).
Укажи тестовые телефоны из demonstration_base.sql.
```

### Промпт 2 — Баг-репорты

```
Оформи 3 баг-репорта по найденным дефектам при локальном тестировании Web+BE:
CORS на [::1], UTF-8 seed на Windows, кириллица в Wasm. Для каждого: шаги,
ожидание/факт, причина, файлы фикса, хеш коммита.
```

### Промпт 3 — Сверка с автотестами

```
Сопоставь ручные TC-A/S/B с существующими Go integration tests и Kotlin commonTest.
Заполни таблицу «Ручная проверка» с Pass/Fail и ссылкой на тест или сценарий UI.
```

---

## Ручная проверка

| # | Тест-кейс | Тип | Ожидание | Результат (2026-07-05) |
|---|-----------|-----|----------|------------------------|
| 1 | TC-A1 | UI + API | Вход `+79990000001` | ✅ Web `localhost:8081`, OTP auto/dev flow |
| 2 | TC-A2 | API | Регистрация нового номера | ✅ `TestAuthVerifyAndLogoutFlow` (`is_new`) |
| 3 | TC-A3 | API | Неверный OTP | ✅ `service/auth/service_test.go` |
| 4 | TC-A4 | API | Rate limit resend | ✅ `TestAuthRequestCodeReturnsDemoCode` + ручная проверка 429 |
| 5 | TC-S1 | UI | Список слотов на неделю | ✅ 40 слотов после `demonstration_base.sql` |
| 6 | TC-S2 | API | Фильтр `route_type` | ✅ `TestSlotsFiltersAndDetails` |
| 7 | TC-S3 | UI | Карточка слота | ✅ Маршалы и адреса на кириллице после BUG-002/003 fix |
| 8 | TC-S4 | API | createBooking + price | ✅ `TestCreateBookingFlowAndIdempotency` (5800 ₽) |
| 9 | TC-S5 | UI/seed | Полный слот | ✅ пн 15:00 `free_seats=0` в demonstration seed |
| 10 | TC-B1 | UI | Список броней | ✅ `TestListAndGetBookings` + UI у Ивана |
| 11 | TC-B2 | UI | Детали брони | ✅ `BookingDetailsStoreTest` (wasm) |
| 12 | TC-B3 | API | Early cancel | ✅ `TestCancelBookingEarlyLateAndAfterStart` |
| 13 | TC-B4 | API | Late cancel | ✅ тот же тест, ветка `< 1h` |
| 14 | — | Auto | Idempotency / concurrency | ✅ `TestCreateBookingConcurrencyDoesNotOverbook` |
| 15 | — | Auto | Domain policies (client) | ✅ `DomainPolicyTest` (wasmJsTest) |

**Команды автопроверки (2026-07-05, все green):**

```bash
cd backend && go test ./... -count=1
cd client && ./gradlew :shared:wasmJsTest
```

**Подготовка демо-данных:**

```bash
cd backend
docker compose -f compose.yaml cp seed/demonstration_base.sql db:/tmp/demonstration_base.sql
docker compose -f compose.yaml exec -T db psql -U apex -d apex -f /tmp/demonstration_base.sql
```

**Известное ограничение среды (не баг MVP):**

- `:shared:testDebugUnitTest` на Windows с кириллицей в пути проекта (`практика`) может падать; обход — `subst X:` (см. `client/README.md`). Для п.4 основной target — `wasmJsTest`.

---

## Сводка

| Метрика | Значение |
|---------|----------|
| Ручных тест-кейсов | 13 (TC-A1…TC-B4) |
| Доп. автотестов в сводке | 2 (concurrency, domain) |
| Найдено багов | 3 |
| Исправлено | 3 (все в `873a8b6`) |
| Блокеров на сдачу п.4 | 0 |

---

## Коммиты

| Хеш | Сообщение |
|-----|-----------|
| `873a8b6` | feat: demonstration database, CORS and web client cyrillic |
| `43ce6ed` | fix: login screen scrolling, otp handling and wasm api url |
| `1c2174b` | docs: update homework journals for parts 1-3 |
| `7818462` | docs: homework p.4 test cases and bug reports |
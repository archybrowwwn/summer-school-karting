# П.3 — Фича 2: Слоты и бронирование (SCR-002–004)

**Дата оформления:** 2026-07-05

---

## Цель

Реализовать просмотр расписания заездов, фильтрацию, карточку слота и оформление брони с выбором числа картов и прокатной экипировки.

---

## Требования

| ID | Описание |
|----|----------|
| US-2, US-3 | Список заездов, фильтры |
| US-4, US-5, US-6 | Запись на заезд, гости, экипировка |
| US-10 | Idempotency-Key при бронировании |
| FR-9, FR-38, FR-9a, FR-10…FR-15 | Каталог, фильтры, бронь, лимиты |
| SCR-002, BS-001, SCR-003, SCR-004, BS-002 | ТЗ экранов |
| LOGIC-002, LOGIC-003, LOGIC-005 | Доступность, цена, фильтры |
| UC-1, UC-3 | Запись на заезд, фильтрация |

**API:**

| operationId | Метод | Путь |
|-------------|-------|------|
| `listSlots` | GET | `/slots` |
| `getSlot` | GET | `/slots/{slotId}` |
| `listInstructors` | GET | `/instructors` |
| `createBooking` | POST | `/bookings` (+ `Idempotency-Key`) |

---

## Что сделано

### Backend

- `handlers/slots.go` — listSlots, getSlot, listInstructors с фильтрами.
- `handlers/bookings.go` + `service/booking/service.go` — атомарное createBooking.
- `storage/postgres/bookings.go` — транзакция, row lock, idempotency.
- Seed-слот `55555555-…` в `migrations/00002_seed_dev.sql` (8 мест, price 2500, rental 300).

### Client

- `SlotListScreen` + `SlotListStore` — список, фильтры (BS-001), пагинация.
- `SlotDetailsScreen` — карточка заезда (SCR-003), CTA «Записаться».
- `BookingFormScreen` + `BookingFormStore` — места 1–5, rental_count, расчёт цены.
- `BookingPriceCalculator`, `AvailabilityPolicy` — domain-политики.
- `RandomIdempotencyKeyFactory` — UUID на каждую попытку брони.

---

## Промпты

### Промпт — Backend slots + booking

```
Реализуй read-only каталог и createBooking для Go API «Апекс».

listSlots: фильтры date_from, date_to, route_type[], instructor_id[], only_available.
getSlot: полная карточка с meeting_point, free_rental_gear.

createBooking:
- seats_count 1..5, rental_count 0..seats_count
- Idempotency-Key в заголовке, повтор с тем же ключом — тот же booking
- Транзакция + FOR UPDATE на slot, проверка free_seats и free_rental_gear
- price_total = price * seats_count + rental_price * rental_count
- Ошибки: slot_full (409), slot_cancelled (410)

Добавь тесты: idempotency, concurrency (no overbook).
```

### Промпт — Client slots + booking form

```
Реализуй SCR-002/BS-001/SCR-003/SCR-004 в CMP shared.

SlotList: табы, фильтры по трассе и маршалу, empty/error states.
SlotDetails: время, трасса, маршал, свободные карты и прокат.
BookingForm:
- stepper мест min(available, 5)
- stepper проката 0..rental_cap
- локальный preview цены по LOGIC-003, после create — price_total с сервера
- Idempotency-Key header на POST /bookings
- success screen BS-002 с переходом в «Мои записи»
```

---

## Ручная проверка

| # | Шаг | Ожидание | Результат (2026-07-05) |
|---|-----|----------|------------------------|
| 1 | `GET /slots?limit=10` | 200, массив items | ✅ `TestCatalogSlotsFiltersAndDetails` |
| 2 | Фильтр `route_type=novice` | Только короткая трасса | ✅ тот же тест |
| 3 | `GET /slots/{id}` | meeting_point, free_seats | ✅ тот же тест |
| 4 | `POST /bookings` seats=2, rental=1 | 201, price_total=5800, free_seats 8→6 | ✅ `TestCreateBookingFlowAndIdempotency` |
| 5 | Повтор с тем же Idempotency-Key | Тот же booking id, count=1 в БД | ✅ тот же тест |
| 6 | 10 параллельных create на 1 место | Ровно 1 успех, остальные 409 | ✅ `TestCreateBookingConcurrencyDoesNotOverbook` |
| 7 | Конфликт idempotency (другой payload) | 409 conflict | ✅ `TestCreateBookingIdempotencyConflict` |
| 8 | Клиент: domain policies | Формулы availability/price | ✅ `DomainPolicyTest` (wasm; Android runner — initializationError, см. п.4) |

**Проверка цены (из integration test):**

- Слот price=2500, rental_price=300.
- seats_count=2, rental_count=1 → `price_total = 2500×2 + 300×1 = 5800` ✅

**Команды:**

```bash
cd backend && go test ./internal/http/handlers/ -run "TestCatalog|TestCreateBooking" -count=1
cd backend && go test ./internal/service/booking/ -count=1
```

---

## Коммиты

| Хеш | Сообщение |
|-----|-----------|
| `1ae4d31` | Add development scaffold: backend, client, API spec |
| `ae81e85` | feat: вход слоты бронь Апекс |
| `4eaebfc` | ui: интерфейс и демо-данные картодрома Апекс |
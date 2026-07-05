# П.3 — Фича 3: Мои брони и отмена (SCR-005–006)

**Дата оформления:** 2026-07-05

---

## Цель

Реализовать список броней клиента, детали брони и отмену с правилом 1 часа: ранняя отмена освобождает карты, поздняя — статус `late_cancel` без освобождения мест.

---

## Требования

| ID | Описание |
|----|----------|
| US-7, US-8 | Мои бронирования, отмена |
| FR-16, FR-17, FR-18, FR-35a | Отмена, late_cancel, история |
| SCR-005, SCR-006, BS-003 | ТЗ экранов |
| LOGIC-004 | [LOGIC-004_Cancellation_24h_Rule.md](../01-analysis/5-mobile-app-spec/09_Logic/LOGIC-004_Cancellation_24h_Rule.md) |
| UC-2 | Отмена записи |

**API:**

| operationId | Метод | Путь |
|-------------|-------|------|
| `listBookings` | GET | `/bookings` |
| `getBooking` | GET | `/bookings/{bookingId}` |
| `cancelBooking` | POST | `/bookings/{bookingId}/cancel` |

---

## Что сделано

### Backend

- `handlers/bookings.go` — list, get, cancel.
- `service/booking/service.go` — логика early/late cancel по `slot.start_at`.
- При early cancel: `free_seats += seats_count`, `free_rental_gear += rental_count`.
- При late cancel: статус `late_cancel`, инвентарь не меняется.
- После старта слота: 422 `slot_started`.

### Client

- `BookingListScreen` + `BookingListStore` — предстоящие/прошедшие, empty state.
- `BookingDetailsScreen` + `BookingDetailsStore` — детали, кнопка отмены, BS-003 confirm sheet.
- `CancellationPolicy` — клиентская подсказка «ранняя/поздняя» до отправки.

---

## Промпты

### Промпт — Backend cancel

```
Реализуй listBookings, getBooking, cancelBooking для Go API «Апекс».

listBookings: только брони текущего client_id, пагинация.
getBooking: 403/404 если чужая бронь.

cancelBooking:
- now < start_at - 1h → status=cancelled, вернуть seats и rental в slot
- start_at - 1h ≤ now < start_at → status=late_cancel, инвентарь не трогать
- now ≥ start_at → 422 slot_started
- Повторная отмена → 409 booking_not_active

Тесты: early, late, after start, concurrent cancel.
```

### Промпт — Client bookings list + cancel

```
Реализуй SCR-005/SCR-006/BS-003 в CMP shared.

BookingList: секции предстоящие/прошедшие, pull-to-refresh, тап → детали.
BookingDetails: слот, места, экипировка, price_total, статус.
Кнопка «Отменить» → bottom sheet BS-003 с текстом ранней/поздней отмены.
После 200 late_cancel — снек «Поздняя отмена», бронь остаётся в списке со статусом.
CancellationPolicy на клиенте для preview до запроса.
```

---

## Ручная проверка

| # | Шаг | Ожидание | Результат (2026-07-05) |
|---|-----|----------|------------------------|
| 1 | `GET /bookings` авторизованным | 200, только свои брони | ✅ `TestListAndGetBookings` |
| 2 | `GET /bookings/{id}` чужой id | 403/404 | ✅ тот же тест |
| 3 | Early cancel (≥1 ч до старта) | 200, status=cancelled, free_seats восстановлены | ✅ `TestCancelBookingEarlyLateAndAfterStart` |
| 4 | Late cancel (<1 ч) | 200, status=late_cancel, free_seats без изменений | ✅ тот же тест |
| 5 | Cancel после старта | 422 slot_started | ✅ тот же тест |
| 6 | Повторный cancel | 409 | ✅ тот же тест |
| 7 | Concurrent early cancel | Места возвращаются один раз | ✅ `TestCancelBookingConcurrencyReturnsAvailabilityOnce` |
| 8 | Client BookingDetailsStore | late vs early UI state | ✅ `BookingDetailsStoreTest` (wasm compile) |

**Команды:**

```bash
cd backend && go test ./internal/http/handlers/ -run "TestListAndGet|TestCancelBooking" -count=1
cd backend && go test ./internal/service/booking/ -run Cancel -count=1
```

---

## Коммиты

| Хеш | Сообщение |
|-----|-----------|
| `1ae4d31` | Add development scaffold: backend, client, API spec |
| `ae81e85` | feat: вход слоты бронь Апекс |
| `4eaebfc` | ui: интерфейс и демо-данные картодрома Апекс |
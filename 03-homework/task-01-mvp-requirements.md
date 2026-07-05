# П.1 — MVP-требования картинг «Апекс»

**Дата оформления:** 2026-07-05  
**Бриф:** [brief-karting.md](../01-analysis/0-customer-brief/brief-karting.md)

---

## Цель

Сгенерировать с ИИ требования к MVP клиентского приложения картинг-центра «Апекс»: user stories, use cases, функциональные и нефункциональные требования, фича-лист и OpenAPI-контракт. Роль — только **Клиент**; админка, CRUD расписания и онлайн-оплата — вне скоупа.

---

## Требования (источники)

| Артефакт | Путь |
|----------|------|
| Уточняющие вопросы | [customer-questions.md](../01-analysis/1-elicitation/customer-questions.md) |
| Описание домена | [domain-description.md](../01-analysis/1-elicitation/domain-description.md) |
| User stories US-1…US-10 | [user-stories.md](../01-analysis/2-requirements/user-stories.md) |
| Use cases UC-1…UC-4 | [use-cases.md](../01-analysis/2-requirements/use-cases.md) |
| FR / BR / NFR | [2-requirements/](../01-analysis/2-requirements/) |
| Фича-лист, SCR/BS | [5-mobile-app-spec/](../01-analysis/5-mobile-app-spec/) |
| OpenAPI (5 доменов) | [api/](../01-analysis/api/) |

**Ключевые инварианты MVP (зафиксированы в требованиях):**

- Лимит брони: до 5 картов на клиента (`max_seats = min(free_seats, route.capacity_cap, 5)`).
- Прокатная экипировка списывается из отдельного фонда `free_rental_gear`.
- Отмена: ≥1 ч до старта — ранняя (места возвращаются); <1 ч — `late_cancel` (места не освобождаются).
- `Idempotency-Key` на `createBooking`.
- Слоты, трассы, маршалы — read-only для клиента.

---

## Что сделано

1. Пройден полный аналитический цикл: бриф → elicitation → требования → дизайн-бриф → модель данных → ТЗ экранов → OpenAPI.
2. Сформированы 10 user stories с трассировкой на FR.
3. Написаны 4 use case с альтернативными потоками (slot_full, гонка, idempotency, late_cancel).
4. Детализированы SCR-001…SCR-007, BS-001…BS-004 и LOGIC-001…LOGIC-008.
5. Описан многофайловый OpenAPI: `auth`, `slots`, `bookings`, `profile`, `instructors`.

---

## Промпты

> Промпты 1–2 и часть п.2 — в [good-prompts.md](../01-analysis/prompts/good-prompts.md). Ниже — полный набор, использованный при выполнении п.1.

### Промпт 1 — User stories из брифа

```
Ты бизнес-аналитик. Вот бриф владельца уличного картинг-центра «Апекс»
(запись на заезды через Telegram, 14 картов, две конфигурации трассы,
маршалы-инструкторы, своя/прокатная экипировка, отмена за 1 час, офлайн-оплата).

Сформируй MVP user stories только для роли Клиент (мобильное приложение).
Формат таблицы: ID, роль, действие, ценность, критерии приёмки, ссылки на FR.

Явно вынеси в Won't: админка владельца, CRUD расписания, онлайн-оплата, рейтинги маршалов.
Задай 3 уточняющих вопроса, если в брифе есть противоречия.
```

### Промпт 2 — Use cases с альтернативными потоками

```
По user stories US-4, US-5, US-8 для картинг-центра «Апекс» напиши use cases
в формате: актор, предусловия, триггер, основной поток, альтернативы/ошибки.

Обязательные инварианты:
- max_seats = min(free_seats, route.capacity_cap, 5)
- прокатная экипировка: rental_count ≤ free_rental_gear, своя — не трогает фонд
- отмена: ≥1 час — места освобождаются; <1 час — late_cancel, без штрафа
- Idempotency-Key на createBooking

Добавь UC для просмотра/фильтрации расписания и OTP-входа.
```

### Промпт 3 — Функциональные требования (FR) с MoSCoW

```
По user stories и брифу картинг-центра «Апекс» составь функциональные требования
в таблице: ID (FR-*), описание, приоритет MoSCoW, источник (бриф / R-*).

Скоуп: только клиентское приложение и API для него.
Группы: авторизация, просмотр слотов, запись, мои брони, отмена, профиль, push.
Вынеси Won't: админка, schedule CRUD, онлайн-оплата, рейтинги, loyalty.
Сохрани русские ID FR-* для трассировки.
```

### Промпт 4 — OpenAPI по доменам

```
По модели данных и use cases для «Апекс» сгенерируй OpenAPI 3.1 в нескольких файлах:
auth, slots, bookings, profile, instructors + common/models.yaml.

Требования:
- Сохранить operationId: requestAuthCode, verifyAuthCode, listSlots, createBooking, cancelBooking и т.д.
- Ошибки в формате { code, message, details? }
- createBooking: заголовок Idempotency-Key (UUID)
- Поля: free_rental_gear, rental_count, price_total, meeting_point, geometry
- Только клиентский API, без admin endpoints
```

### Промпт 5 — Фича-лист и ТЗ экранов

```
По FR и user stories для «Апекс» создай feature-list.md с картой навигации (mermaid)
и таблицей экранов SCR-001…SCR-007, BS-001…BS-005.

Для каждого Critical-экрана напиши ТЗ по шаблону: User Story, навигация,
состояния Loading/Content/Empty/Error, API operationId, критерии приёмки.
Вынеси оценку маршала (BS-005) в Phase 2.
```

---

## Ручная проверка

| # | Проверка | Ожидание | Результат (2026-07-05) |
|---|----------|----------|------------------------|
| 1 | US покрывают только роль «Клиент» | 10 stories, без admin/marshal UI | ✅ US-1…US-10 в [user-stories.md](../01-analysis/2-requirements/user-stories.md) |
| 2 | UC содержат альтернативные потоки | E1 slot_full, A1 late_cancel, E4 idempotency | ✅ UC-1…UC-4 |
| 3 | Трассировка US → FR → SCR | Ссылки согласованы | ✅ Проверено по таблицам в spec |
| 4 | Won't вынесен явно | Нет админки, CRUD, онлайн-оплаты | ✅ В FR и feature-list §7 |
| 5 | OpenAPI lint | 5 доменов без ошибок | ✅ `npm run lint` в `01-analysis/api` — validated |
| 6 | Числовые инварианты | 5 картов, 1 ч отмена, rental отдельно | ✅ В UC, LOGIC-002/004, data-model |

---

## Коммиты

| Хеш | Сообщение |
|-----|-----------|
| `5fd7c35` | Initial commit: аналитика и ТЗ мобильного приложения «Апекс» |
| `056c44b` | docs(analysis): п.1 — MVP-требования картинг «Апекс» (user stories, use cases, FR) |
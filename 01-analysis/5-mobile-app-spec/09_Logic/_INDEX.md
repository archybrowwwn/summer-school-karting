# 09. Логики — индекс

> Переиспользуемая бизнес- и UI-логика приложения «Апекс». Шаблон — [_LOGIC_TEMPLATE.md](../_LOGIC_TEMPLATE.md).

**Статус:** Актуален · **Дата:** 2026-07-04

---

## Реестр логик

| ID | Логика | Приоритет | Назначение | Применяется на |
|----|--------|-----------|------------|----------------|
| **LOGIC-001** | [OTP-авторизация](LOGIC-001_OTP_Authorization.md) | Critical | Вход по телефону (3 шага), JWT-сессия | SCR-001, SCR-007 |
| **LOGIC-002** | [Доступность мест и экипировки](LOGIC-002_Kart_Availability_Calculation.md) | Critical | `max_seats`, `rental_count`, CTA | SCR-002, SCR-003, SCR-004 |
| **LOGIC-003** | [Итоговая цена брони](LOGIC-003_Session_Pricing_Calculation.md) | High | Серверный `price_total` (R-005) | SCR-004, BS-002, SCR-005, SCR-006 |
| **LOGIC-004** | [Отмена: правило 1 часа](LOGIC-004_Cancellation_24h_Rule.md) | Critical | Ранняя / поздняя отмена (R-021) | SCR-006, BS-003 |
| **LOGIC-005** | [Фильтрация заездов](LOGIC-005_Slot_Filtering_and_Sorting.md) | High | `listSlots` + фильтры BS-001 | SCR-002, BS-001 |
| **LOGIC-006** | [Схема трассы и место сбора](LOGIC-006_Track_Map_and_Position.md) | Medium | `route.geometry`, meeting_point | SCR-003, SCR-006, BS-004 |
| **LOGIC-007** | [Запрос push-разрешения](LOGIC-007_Push_Permission_Request.md) | Medium | Push после первой брони | BS-002 |
| **LOGIC-008** | [Паттерн состояний экрана](LOGIC-008_Screen_State_Pattern.md) | High | Loading → Content → Empty → Error | Все экраны с запросами |
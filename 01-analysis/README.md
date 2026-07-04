# День 1 — Анализ · Картинг-центр «Апекс»

Артефакты аналитика по проекту летней школы. Структура повторяет процесс с лекций:
от брифа заказчика до ТЗ для разработки.

## Маршрут по этапам

| Этап | Папка | Что внутри |
| :-- | :-- | :-- |
| **Вход** | [0-customer-brief/](0-customer-brief/) | [brief-karting.md](0-customer-brief/brief-karting.md) — бриф Дениса |
| **1. Выявление** | [1-elicitation/](1-elicitation/) | [customer-questions.md](1-elicitation/customer-questions.md), [domain-description.md](1-elicitation/domain-description.md) |
| **2. Требования** | [2-requirements/](2-requirements/) | business · functional · non-functional · [user-stories](2-requirements/user-stories.md) · [use-cases](2-requirements/use-cases.md) |
| **Бриф для дизайна** | [3-design-brief/](3-design-brief/) | [design-brief.md](3-design-brief/design-brief.md), экраны SCR/BS |
| **3. Проектирование** | [4-design/](4-design/) | [data-model.md](4-design/data-model.md), [api-sequence.md](4-design/api-sequence.md) |
| **4. ТЗ** | [5-mobile-app-spec/](5-mobile-app-spec/) | [feature-list.md](5-mobile-app-spec/feature-list.md), SCR/BS, логики |
| **API** | [api/](api/) | OpenAPI (auth, slots, bookings, profile, instructors) |

## Статус

| Артефакт | Статус |
|----------|--------|
| Бриф + уточнения скоупа (R-004…R-028) | ✅ |
| User stories, use cases, FR/BR/NFR | ✅ |
| Модель данных, API sequence | ✅ |
| Фича-лист и ТЗ экранов | ✅ (черновик) |
| OpenAPI | ✅ |

> **Передача в разработку:** требования + модель данных + API + ТЗ экранов.
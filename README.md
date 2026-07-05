# Summer School — картинг «Апекс»

Учебный проект: клиентское мобильное приложение для записи на заезды в картинг-центре «Апекс».

## Структура

| Папка | Содержимое |
|-------|------------|
| `01-analysis/` | Аналитика и ТЗ по проекту «Апекс» (бриф, требования, экраны, OpenAPI) |
| `02-development/` | Планы реализации backend и CMP-клиента |
| `03-homework/` | Журнал домашнего задания летней школы (п.1–4) |
| `backend/` | Go API + PostgreSQL для картинг-центра «Апекс» |
| `client/` | Kotlin Compose Multiplatform — Android, Web, iOS |

## Быстрый старт

Подробные команды — в [LOCAL_DEV_GUIDE.md](LOCAL_DEV_GUIDE.md).

```bash
# Backend
cd backend && docker compose --profile db up -d db && make migrate && make run

# Web-клиент
cd client && ./gradlew :webApp:wasmJsBrowserDevelopmentRun
```

## Репозиторий

https://github.com/archybrowwwn/summer-school-karting
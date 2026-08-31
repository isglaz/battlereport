# ⚔️ BattleReport

**BattleReport** is a web platform for tabletop wargame enthusiasts — find your next game without buying blind.

## Why?

There are hundreds of tabletop wargames out there, and it's hard to know where to start.
Instead of endless marketing descriptions and generic reviews, BattleReport gives you
**real battle reports from real players**, all gathered in one place.

## ✨ Features

- 📋 **Battle reports** — see how a game actually plays before you invest in it
- 🔍 **One place for everything** — no more scattered forums and blogs
- 🗳️ **Community-driven** — honest impressions from people who've been there
- ✍️ **Share your own** — write up your sessions and help others choose

## 🛠️ Tech Stack

The entire app is written in **pure Kotlin** — backend and frontend alike:

| Layer    | Stack                              |
|----------|------------------------------------|
| Backend  | **Ktor** + Exposed + PostgreSQL    |
| Frontend | **Kobweb** (Kotlin/JS + Compose)   |
| Build    | Gradle (Kotlin DSL)                |

## 🚀 Запуск локально

Нужен Docker.

```sh
cp .env.example .env          # правки не обязательны, дефолты рабочие
docker compose up -d postgres # БД
./deploy/migrate.sh update    # схема
docker compose up -d          # бэк на :8080, фронт на :8081
```

Миграции — отдельный шаг: сервер их не применяет, схему создаёт только Liquibase.
Подробности — в [migrations/README.md](migrations/README.md).

## ⚙️ Конфигурация

Единственный источник — переменные окружения. Локально они лежат в `.env`
(создаётся из `.env.example`, в репозиторий не коммитится), на проде — в `.env`
на сервере, который заполняет TeamCity. Полный список — в `.env.example`.

## 📦 Деплой

```
Build (образы) -> Migrate (liquibase update) -> Deploy (compose up)
```

Пайплайн описан в `.teamcity/settings.kts`, шаги — в `deploy/`.
Порядок жёсткий: миграции применяются до подъёма нового кода.

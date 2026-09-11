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

## 🚀 Running locally

Docker is required.

```sh
cp .env.example .env          # edits are optional, the defaults work
docker compose up -d postgres # database
./deploy/migrate.sh update    # schema
docker compose up -d          # backend on :8080, frontend on :8081
```

Migrations are a separate step: the server does not apply them, only Liquibase creates the schema.
Details are in [migrations/README.md](migrations/README.md).

## ⚙️ Configuration

Environment variables are the single source. Locally they live in `.env`
(created from `.env.example`, not committed), in production in the `.env`
on the server, which TeamCity fills in. The full list is in `.env.example`.

## 📦 Deployment

```
Build (images) -> Migrate (liquibase update) -> Deploy (compose up)
```

The pipeline is described in `.teamcity/settings.kts`, the steps in `deploy/`.
The order is strict: migrations are applied before the new code comes up.

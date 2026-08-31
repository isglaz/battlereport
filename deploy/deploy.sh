#!/bin/sh
# Деплой на прод: выполняется на сервере, TeamCity зовёт его по SSH.
#
# Порядок жёсткий: миграции применяются ДО подъёма нового кода, иначе новый сервер
# стартует на старой схеме.
#
# Ожидает, что рядом с compose-файлами лежит .env с REGISTRY, TAG и координатами БД.
set -eu

REPO_ROOT=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
cd "$REPO_ROOT"

COMPOSE="docker compose -f compose.yaml -f compose.prod.yaml"

echo "==> Забираем образы"
$COMPOSE pull

echo "==> Поднимаем БД"
$COMPOSE up -d postgres

echo "==> Накатываем миграции"
# .env читает compose, но migrate.sh запускается напрямую — подгружаем сами.
set -a
. ./.env
set +a
COMPOSE_NETWORK=$(docker network ls --filter name=battlereport --format '{{.Name}}' | head -n 1)
export COMPOSE_NETWORK
./deploy/migrate.sh update

echo "==> Поднимаем приложение"
$COMPOSE up -d

echo "==> Готово"
$COMPOSE ps

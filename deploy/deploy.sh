#!/bin/sh
# Production deployment: runs on the server, TeamCity invokes it over SSH.
#
# The order is strict: migrations are applied BEFORE the new code comes up, otherwise the
# new server starts against the old schema.
#
# Expects an .env next to the compose files holding REGISTRY, TAG and the database coordinates.
set -eu

REPO_ROOT=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
cd "$REPO_ROOT"

COMPOSE="docker compose -f compose.yaml -f compose.prod.yaml"

echo "==> Pulling images"
$COMPOSE pull

echo "==> Starting the database"
$COMPOSE up -d postgres

echo "==> Applying migrations"
# compose reads .env, but migrate.sh is invoked directly - load it ourselves.
set -a
. ./.env
set +a
COMPOSE_NETWORK=$(docker network ls --filter name=battlereport --format '{{.Name}}' | head -n 1)
export COMPOSE_NETWORK
./deploy/migrate.sh update

echo "==> Starting the application"
$COMPOSE up -d

echo "==> Done"
$COMPOSE ps

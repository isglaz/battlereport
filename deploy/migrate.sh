#!/bin/sh
# Накат миграций официальным образом Liquibase. Один и тот же скрипт локально и в TeamCity:
# каталог migrations/liquibase самодостаточен, собирать нечего.
#
#   ./deploy/migrate.sh            # update
#   ./deploy/migrate.sh status
#   ./deploy/migrate.sh update-sql # показать SQL, ничего не применяя
#   ./deploy/migrate.sh rollback-count --count=1
#
# Координаты БД — из окружения (.env локально, параметры TeamCity на проде).
set -eu

# Версия держится в согласии с `liquibase` в gradle/libs.versions.toml,
# чтобы тесты и прод катали миграции одной и той же версией.
LIQUIBASE_IMAGE="liquibase/liquibase:4.32"

REPO_ROOT=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
COMMAND=${1:-update}
[ $# -gt 0 ] && shift

# Git Bash на Windows переписывает пути, похожие на unix-овые, в windows-овые, и путь
# назначения тома превращается в мусор — том молча не монтируется. Отключаем конвертацию.
export MSYS_NO_PATHCONV=1
export MSYS2_ARG_CONV_EXCL='*'

: "${DB_JDBC_URL:?DB_JDBC_URL не задан (см. .env.example)}"
: "${DB_USER:?DB_USER не задан}"
: "${DB_PASSWORD:?DB_PASSWORD не задан}"

# Контейнеру нужен доступ к БД. Если Postgres поднят этим же compose-проектом,
# он живёт в его сети — подключаемся к ней, тогда хост `postgres` из DB_JDBC_URL резолвится.
NETWORK_ARG=""
if [ -n "${COMPOSE_NETWORK:-}" ]; then
    NETWORK_ARG="--network=${COMPOSE_NETWORK}"
elif docker network inspect battlereport_default >/dev/null 2>&1; then
    NETWORK_ARG="--network=battlereport_default"
fi

echo "Running liquibase ${COMMAND} against ${DB_JDBC_URL}"

# shellcheck disable=SC2086
exec docker run --rm $NETWORK_ARG \
    -v "${REPO_ROOT}/migrations/liquibase:/liquibase/changelog:ro" \
    "$LIQUIBASE_IMAGE" \
    --changelog-file=changelog/db.changelog-master.yaml \
    --url="$DB_JDBC_URL" \
    --username="$DB_USER" \
    --password="$DB_PASSWORD" \
    --log-level=info \
    "$COMMAND" "$@"

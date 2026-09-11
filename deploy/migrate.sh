#!/bin/sh
# Applies migrations with the official Liquibase image. The same script runs locally and in
# TeamCity: the migrations/liquibase directory is self-contained, there is nothing to build.
#
#   ./deploy/migrate.sh            # update
#   ./deploy/migrate.sh status
#   ./deploy/migrate.sh update-sql # print the SQL without applying anything
#   ./deploy/migrate.sh rollback-count --count=1
#
# Database coordinates come from the environment (.env locally, TeamCity parameters in production).
set -eu

# The version is kept in sync with `liquibase` in gradle/libs.versions.toml so that tests and
# production run migrations with the very same version.
LIQUIBASE_IMAGE="liquibase/liquibase:4.32"

REPO_ROOT=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
COMMAND=${1:-update}
[ $# -gt 0 ] && shift

# Git Bash on Windows rewrites unix-looking paths into windows ones, and the volume
# destination path turns into garbage - the volume silently fails to mount. Disable the conversion.
export MSYS_NO_PATHCONV=1
export MSYS2_ARG_CONV_EXCL='*'

: "${DB_JDBC_URL:?DB_JDBC_URL is not set (see .env.example)}"
: "${DB_USER:?DB_USER is not set}"
: "${DB_PASSWORD:?DB_PASSWORD is not set}"

# The container needs access to the database. If Postgres was started by this same compose
# project it lives in its network - join that network so the `postgres` host from DB_JDBC_URL resolves.
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

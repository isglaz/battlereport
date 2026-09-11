# Database migrations

The directory is self-contained: there is no build here, no `build.gradle.kts` and no jar.
Liquibase reads these files straight from disk, so migrations can be applied by anything
able to read a directory - local docker, TeamCity, or by hand through the CLI.

```
liquibase/
├── db.changelog-master.yaml   # only includeAll over the version folders
└── v-1.0.0/
    └── 01-init.sql            # liquibase formatted sql
```

## How to apply

```sh
./deploy/migrate.sh              # update
./deploy/migrate.sh status       # what the database is missing
./deploy/migrate.sh validate     # duplicate ids and broken checksums
./deploy/migrate.sh update-sql   # print the SQL without applying anything
```

The script takes the database coordinates from the environment (`DB_JDBC_URL`, `DB_USER`,
`DB_PASSWORD`); locally they arrive from the `.env` in the repository root.

## How to add a migration

A new schema version means a new `v-<version>/` folder, picked up by `includeAll` on its own.
Inside a folder the files are applied in lexicographic order, so the name starts with a
number: `01-`, `02-`, ...

Rules that save your nerves:

* **An applied changeset is never edited.** Liquibase computes a checksum, and editing an
  already applied file breaks `update` on every environment where it has been applied.
  If a change is needed - add a new changeset.
* Every changeset has a `--rollback`: without it a release cannot be rolled back.
* A plpgsql body contains `;`, so such a changeset needs `splitStatements:false` -
  otherwise Liquibase cuts the function into pieces.

## Where else this directory is used

The `:server` tests (`DatabaseTestBase`) apply this same changelog to Postgres in
Testcontainers. A broken migration fails in the tests rather than in production.

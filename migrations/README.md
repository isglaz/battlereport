# Миграции БД

Каталог самодостаточен: здесь нет сборки, нет `build.gradle.kts` и нет jar-а.
Liquibase читает эти файлы прямо с диска, поэтому накатить миграции может что угодно,
что умеет читать каталог — локальный docker, TeamCity, руками через CLI.

```
liquibase/
├── db.changelog-master.yaml   # только includeAll на папки версий
└── v-1.0.0/
    └── 01-init.sql            # liquibase formatted sql
```

## Как накатить

```sh
./deploy/migrate.sh              # update
./deploy/migrate.sh status       # чего не хватает в БД
./deploy/migrate.sh validate     # дубли id и битые checksum
./deploy/migrate.sh update-sql   # показать SQL, ничего не применяя
```

Скрипт берёт координаты БД из окружения (`DB_JDBC_URL`, `DB_USER`, `DB_PASSWORD`);
локально они приезжают из `.env` в корне репозитория.

## Как добавить миграцию

Новая версия схемы — новая папка `v-<версия>/`, она подхватывается `includeAll` сама.
Внутри папки файлы применяются в лексикографическом порядке, поэтому имя начинается
с номера: `01-`, `02-`, ...

Правила, которые экономят нервы:

* **Применённый changeset не редактируют.** Liquibase считает checksum, и правка
  уже накатанного файла ломает `update` на всех средах, где он применён.
  Нужно изменение — новый changeset.
* У каждого changeset есть `--rollback`: без него откат релиза невозможен.
* Тело plpgsql содержит `;`, поэтому такому changeset нужен `splitStatements:false` —
  иначе Liquibase разрежет функцию на куски.

## Где ещё используется этот каталог

Тесты `:server` (`DatabaseTestBase`) накатывают этот же changelog на Postgres
в Testcontainers. Сломанная миграция падает в тестах, а не на проде.

--liquibase formatted sql
--changeset isglaz:1.0.0-01-extensions
CREATE EXTENSION IF NOT EXISTS pgcrypto;
--rollback DROP EXTENSION IF EXISTS pgcrypto;

--changeset isglaz:1.0.0-01-users
CREATE TABLE users (
    id           UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    name         TEXT        NOT NULL,
    handle       TEXT        NOT NULL,
    joined       TIMESTAMPTZ NOT NULL DEFAULT now(),
    bio          TEXT        NOT NULL DEFAULT '',
    -- ключ в облачном хранилище, напр. "users/42/avatar.webp"; NULL → рисуем avatar_color
    avatar_key   TEXT,
    -- oklch-строка, fallback когда avatar_key IS NULL
    avatar_color TEXT        NOT NULL DEFAULT 'oklch(0.27 0 0)',
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- handle уникален без учёта регистра: @Ilya и @ilya — один и тот же человек
CREATE UNIQUE INDEX users_handle_lower_key ON users (lower(handle));
--rollback DROP TABLE users;

--changeset isglaz:1.0.0-01-wargames
CREATE TABLE wargames (
    id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    title      TEXT        NOT NULL,
    year       INTEGER     NOT NULL,
    publisher  TEXT        NOT NULL,
    designer   TEXT        NOT NULL,
    -- fallback-подпись коробки, когда cover_key IS NULL
    box_label  TEXT        NOT NULL,
    tagline    TEXT        NOT NULL,
    descr      TEXT        NOT NULL,
    cover_key  TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX wargames_title_idx ON wargames (title);
--rollback DROP TABLE wargames;

--changeset isglaz:1.0.0-01-reports
CREATE TABLE reports (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    author_id   UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    wargame_id  UUID        NOT NULL REFERENCES wargames (id) ON DELETE RESTRICT,
    title       TEXT        NOT NULL,
    -- дата партии, без времени
    date        DATE        NOT NULL,
    likes       INTEGER     NOT NULL DEFAULT 0,
    views       INTEGER     NOT NULL DEFAULT 0,
    -- соперник может быть не зарегистрирован в системе
    opponent_id UUID        REFERENCES users (id) ON DELETE SET NULL,
    duration    TEXT        NOT NULL DEFAULT '',
    preview     TEXT        NOT NULL DEFAULT '',
    -- markdown
    body        TEXT        NOT NULL DEFAULT '',
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT reports_likes_non_negative CHECK (likes >= 0),
    CONSTRAINT reports_views_non_negative CHECK (views >= 0),
    CONSTRAINT reports_opponent_not_author CHECK (opponent_id IS NULL OR opponent_id <> author_id)
);

CREATE INDEX reports_author_id_idx  ON reports (author_id);
CREATE INDEX reports_wargame_id_idx ON reports (wargame_id);
-- лента: свежие отчёты первыми
CREATE INDEX reports_date_idx       ON reports (date DESC);
--rollback DROP TABLE reports;

--changeset isglaz:1.0.0-01-comments
CREATE TABLE comments (
    id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    report_id  UUID        NOT NULL REFERENCES reports (id) ON DELETE CASCADE,
    author_id  UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    -- self-reference: дерево ответов собирается в сервисном слое
    parent_id  UUID        REFERENCES comments (id) ON DELETE CASCADE,
    date       TIMESTAMPTZ NOT NULL DEFAULT now(),
    text       TEXT        NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- загрузка всей ветки обсуждения одного отчёта
CREATE INDEX comments_report_id_date_idx ON comments (report_id, date);
CREATE INDEX comments_parent_id_idx      ON comments (parent_id);
CREATE INDEX comments_author_id_idx      ON comments (author_id);
--rollback DROP TABLE comments;

--changeset isglaz:1.0.0-01-report-images
CREATE TABLE report_images (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    report_id   UUID        NOT NULL REFERENCES reports (id) ON DELETE CASCADE,
    -- ключ в облачном хранилище, как avatar_key/cover_key
    storage_key TEXT        NOT NULL,
    -- порядок в галерее; markdown-тело ссылается на storage_key напрямую
    position    INTEGER     NOT NULL DEFAULT 0,
    caption     TEXT        NOT NULL DEFAULT '',
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT report_images_position_non_negative CHECK (position >= 0)
);

-- один и тот же файл не должен числиться за отчётом дважды
CREATE UNIQUE INDEX report_images_report_id_storage_key_key ON report_images (report_id, storage_key);
-- выдача галереи в заданном порядке
CREATE INDEX report_images_report_id_position_idx ON report_images (report_id, position);
--rollback DROP TABLE report_images;

--changeset isglaz:1.0.0-01-drafts
CREATE TABLE drafts (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    author_id   UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    title       TEXT        NOT NULL DEFAULT '',
    body        TEXT        NOT NULL DEFAULT '',
    -- в черновике игра и соперник ещё могут быть не выбраны
    wargame_id  UUID        REFERENCES wargames (id) ON DELETE SET NULL,
    opponent_id UUID        REFERENCES users (id) ON DELETE SET NULL,
    date        DATE,
    duration    TEXT        NOT NULL DEFAULT '',
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX drafts_author_id_updated_at_idx ON drafts (author_id, updated_at DESC);
--rollback DROP TABLE drafts;

-- Тело plpgsql содержит ';', поэтому splitStatements:false — иначе Liquibase
-- разрежет функцию на куски по точке с запятой.
--changeset isglaz:1.0.0-01-updated-at-function splitStatements:false
CREATE OR REPLACE FUNCTION set_updated_at() RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
--rollback DROP FUNCTION IF EXISTS set_updated_at() CASCADE;

-- Один триггер на все таблицы: updated_at обновляется без участия приложения.
--changeset isglaz:1.0.0-01-updated-at-triggers
CREATE TRIGGER users_set_updated_at    BEFORE UPDATE ON users    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER wargames_set_updated_at BEFORE UPDATE ON wargames FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER reports_set_updated_at  BEFORE UPDATE ON reports  FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER comments_set_updated_at BEFORE UPDATE ON comments FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER drafts_set_updated_at   BEFORE UPDATE ON drafts   FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER report_images_set_updated_at BEFORE UPDATE ON report_images FOR EACH ROW EXECUTE FUNCTION set_updated_at();
--rollback DROP TRIGGER IF EXISTS users_set_updated_at ON users;
--rollback DROP TRIGGER IF EXISTS wargames_set_updated_at ON wargames;
--rollback DROP TRIGGER IF EXISTS reports_set_updated_at ON reports;
--rollback DROP TRIGGER IF EXISTS comments_set_updated_at ON comments;
--rollback DROP TRIGGER IF EXISTS drafts_set_updated_at ON drafts;
--rollback DROP TRIGGER IF EXISTS report_images_set_updated_at ON report_images;

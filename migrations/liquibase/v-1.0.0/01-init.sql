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
    -- key in the cloud storage, e.g. "users/42/avatar.webp"; NULL -> draw avatar_color
    avatar_key   TEXT,
    -- oklch string, fallback when avatar_key IS NULL
    avatar_color TEXT        NOT NULL DEFAULT 'oklch(0.27 0 0)',
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- handle is unique case-insensitively: @Ilya and @ilya are the same person
CREATE UNIQUE INDEX users_handle_lower_key ON users (lower(handle));
--rollback DROP TABLE users;

--changeset isglaz:1.0.0-01-wargames
CREATE TABLE wargames (
    id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    title      TEXT        NOT NULL,
    year       INTEGER     NOT NULL,
    publisher  TEXT        NOT NULL,
    designer   TEXT        NOT NULL,
    -- fallback box caption when cover_key IS NULL
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
    -- date of the session, without time
    date        DATE        NOT NULL,
    likes       INTEGER     NOT NULL DEFAULT 0,
    views       INTEGER     NOT NULL DEFAULT 0,
    -- the opponent may not be registered in the system
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
-- feed: newest reports first
CREATE INDEX reports_date_idx       ON reports (date DESC);
--rollback DROP TABLE reports;

--changeset isglaz:1.0.0-01-comments
CREATE TABLE comments (
    id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    report_id  UUID        NOT NULL REFERENCES reports (id) ON DELETE CASCADE,
    author_id  UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    -- self-reference: the reply tree is assembled in the service layer
    parent_id  UUID        REFERENCES comments (id) ON DELETE CASCADE,
    date       TIMESTAMPTZ NOT NULL DEFAULT now(),
    text       TEXT        NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- loading the whole discussion thread of a single report
CREATE INDEX comments_report_id_date_idx ON comments (report_id, date);
CREATE INDEX comments_parent_id_idx      ON comments (parent_id);
CREATE INDEX comments_author_id_idx      ON comments (author_id);
--rollback DROP TABLE comments;

--changeset isglaz:1.0.0-01-report-images
CREATE TABLE report_images (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    report_id   UUID        NOT NULL REFERENCES reports (id) ON DELETE CASCADE,
    -- key in the cloud storage, like avatar_key/cover_key
    storage_key TEXT        NOT NULL,
    -- order in the gallery; the markdown body references storage_key directly
    position    INTEGER     NOT NULL DEFAULT 0,
    caption     TEXT        NOT NULL DEFAULT '',
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT report_images_position_non_negative CHECK (position >= 0)
);

-- the same file must not be attached to a report twice
CREATE UNIQUE INDEX report_images_report_id_storage_key_key ON report_images (report_id, storage_key);
-- serving the gallery in the given order
CREATE INDEX report_images_report_id_position_idx ON report_images (report_id, position);
--rollback DROP TABLE report_images;

--changeset isglaz:1.0.0-01-drafts
CREATE TABLE drafts (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    author_id   UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    title       TEXT        NOT NULL DEFAULT '',
    body        TEXT        NOT NULL DEFAULT '',
    -- in a draft the game and the opponent may not be chosen yet
    wargame_id  UUID        REFERENCES wargames (id) ON DELETE SET NULL,
    opponent_id UUID        REFERENCES users (id) ON DELETE SET NULL,
    date        DATE,
    duration    TEXT        NOT NULL DEFAULT '',
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX drafts_author_id_updated_at_idx ON drafts (author_id, updated_at DESC);
--rollback DROP TABLE drafts;

-- The plpgsql body contains ';', hence splitStatements:false - otherwise Liquibase would
-- cut the function into pieces at the semicolons.
--changeset isglaz:1.0.0-01-updated-at-function splitStatements:false
CREATE OR REPLACE FUNCTION set_updated_at() RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
--rollback DROP FUNCTION IF EXISTS set_updated_at() CASCADE;

-- One trigger for all tables: updated_at is maintained without the application's involvement.
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

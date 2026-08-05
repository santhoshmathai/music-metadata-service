CREATE SEQUENCE artist_rotation_position_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE artists (
    id UUID PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    rotation_position BIGINT NOT NULL DEFAULT nextval('artist_rotation_position_seq'),
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_artist_rotation_position UNIQUE (rotation_position),
    CONSTRAINT ck_artist_name_not_blank CHECK (length(trim(name)) > 0)
);

CREATE TABLE artist_aliases (
    id UUID PRIMARY KEY,
    artist_id UUID NOT NULL REFERENCES artists(id) ON DELETE CASCADE,
    name VARCHAR(200) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_alias_name_not_blank CHECK (length(trim(name)) > 0)
);

CREATE UNIQUE INDEX uq_artist_alias_case_insensitive
    ON artist_aliases (artist_id, lower(name));

CREATE TABLE tracks (
    id UUID PRIMARY KEY,
    artist_id UUID NOT NULL REFERENCES artists(id) ON DELETE CASCADE,
    title VARCHAR(300) NOT NULL,
    genre VARCHAR(100) NOT NULL,
    duration_ms INTEGER NOT NULL,
    release_date DATE,
    album_name VARCHAR(300),
    isrc VARCHAR(12),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_track_title_not_blank CHECK (length(trim(title)) > 0),
    CONSTRAINT ck_track_genre_not_blank CHECK (length(trim(genre)) > 0),
    CONSTRAINT ck_track_duration CHECK (duration_ms > 0 AND duration_ms <= 86400000)
);

CREATE INDEX idx_tracks_artist_created ON tracks (artist_id, created_at DESC);
CREATE INDEX idx_tracks_artist_title ON tracks (artist_id, title);
CREATE UNIQUE INDEX uq_track_isrc ON tracks (isrc) WHERE isrc IS NOT NULL;

CREATE TABLE artist_daily_features (
    feature_date DATE PRIMARY KEY,
    artist_id UUID NOT NULL REFERENCES artists(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_artist_daily_features_artist ON artist_daily_features (artist_id);

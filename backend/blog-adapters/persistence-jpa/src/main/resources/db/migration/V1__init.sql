-- =========================================================
-- V1__init.sql
-- Initial schema for event-sourced blog/portfolio system
-- =========================================================

-- ===============================
-- 1. EVENT STORE (WRITE MODEL)
-- ===============================
CREATE TABLE domain_events (
    aggregate_type      VARCHAR(100) NOT NULL,   -- e.g. 'Category', 'Post', 'Tag', 'AuthorProfile'
    aggregate_id        VARCHAR(100) NOT NULL,   -- domain id as string (CategoryId, PostId, etc.)
    version             INT NOT NULL,            -- strictly increasing 1,2,3,... per aggregate
    event_type          VARCHAR(150) NOT NULL,   -- e.g. 'CategoryCreated'
    event_payload       JSONB NOT NULL,          -- serialized event body
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT domain_events_pk
        PRIMARY KEY (aggregate_type, aggregate_id, version)
)
PARTITION BY LIST (aggregate_type);

-- ---------------------------------
-- Partitions per aggregate_type
-- ---------------------------------
CREATE TABLE domain_events_category
    PARTITION OF domain_events
    FOR VALUES IN ('Category');

CREATE TABLE domain_events_post
    PARTITION OF domain_events
    FOR VALUES IN ('Post');

CREATE TABLE domain_events_tag
    PARTITION OF domain_events
    FOR VALUES IN ('Tag');

CREATE TABLE domain_events_author_profile
    PARTITION OF domain_events
    FOR VALUES IN ('AuthorProfile');

-- ---------------------------------
-- Indexes on partitions
-- ---------------------------------
CREATE INDEX idx_domain_events_category_stream
    ON domain_events_category (aggregate_id, version ASC);

CREATE INDEX idx_domain_events_post_stream
    ON domain_events_post (aggregate_id, version ASC);

CREATE INDEX idx_domain_events_tag_stream
    ON domain_events_tag (aggregate_id, version ASC);

CREATE INDEX idx_domain_events_author_profile_stream
    ON domain_events_author_profile (aggregate_id, version ASC);

-- time-based index for debugging / analytics / ops
CREATE INDEX idx_domain_events_category_created_at
    ON domain_events_category (created_at);

CREATE INDEX idx_domain_events_post_created_at
    ON domain_events_post (created_at);

CREATE INDEX idx_domain_events_tag_created_at
    ON domain_events_tag (created_at);

CREATE INDEX idx_domain_events_author_profile_created_at
    ON domain_events_author_profile (created_at);

-- ===============================
-- 2. READ MODEL / PROJECTIONS
-- ===============================

-- ---------------------------------
-- CATEGORY VIEW
-- ---------------------------------
CREATE TABLE category_view (
    id                      VARCHAR(100) PRIMARY KEY,
    name                    TEXT        NOT NULL,
    slug                    TEXT        NOT NULL,
    default_image           TEXT,
    status                  VARCHAR(20) NOT NULL,    -- e.g. 'ACTIVE' | 'ARCHIVED'
    created_at              TIMESTAMPTZ NOT NULL,
    updated_at              TIMESTAMPTZ,
    archived_at             TIMESTAMPTZ,
    last_version_applied    INT NOT NULL DEFAULT 0
);

CREATE INDEX idx_category_view_status
    ON category_view (status);

CREATE INDEX idx_category_view_name
    ON category_view (name);

CREATE UNIQUE INDEX idx_category_view_slug
    ON category_view (slug);

-- ---------------------------------
-- TAG VIEW
-- ---------------------------------
CREATE TABLE tag_view (
    id                      VARCHAR(100) PRIMARY KEY,
    name                    TEXT        NOT NULL,
    slug                    TEXT        NOT NULL,
    status                  VARCHAR(20) NOT NULL,    -- 'ACTIVE' | 'ARCHIVED'
    created_at              TIMESTAMPTZ NOT NULL,
    updated_at              TIMESTAMPTZ,
    archived_at             TIMESTAMPTZ,
    last_version_applied    INT NOT NULL DEFAULT 0
);

CREATE INDEX idx_tag_view_status
    ON tag_view (status);

CREATE UNIQUE INDEX idx_tag_view_slug
    ON tag_view (slug);

-- ---------------------------------
-- POST VIEW
-- ---------------------------------
CREATE TABLE post_view (
    id                      VARCHAR(100) PRIMARY KEY,
    author_id               VARCHAR(100) NOT NULL,
    title                   TEXT        NOT NULL,
    slug                    TEXT        NOT NULL,
    summary                 TEXT,
    body_markdown           TEXT,
    cover_image             TEXT,
    category_id             VARCHAR(100),
    status                  VARCHAR(20) NOT NULL,     -- 'DRAFT' | 'PUBLISHED' | 'ARCHIVED'
    created_at              TIMESTAMPTZ NOT NULL,
    updated_at              TIMESTAMPTZ,
    published_at            TIMESTAMPTZ,
    archived_at             TIMESTAMPTZ,
    last_version_applied    INT NOT NULL DEFAULT 0
);

-- Table relationship
ALTER TABLE post_view
    ADD CONSTRAINT fk_post_category
    FOREIGN KEY (category_id)
    REFERENCES category_view(id)
    ON UPDATE CASCADE
    ON DELETE SET NULL;

-- Indexes
CREATE INDEX idx_post_view_status_published
    ON post_view (status, published_at DESC);

CREATE UNIQUE INDEX idx_post_view_slug
    ON post_view (slug);

CREATE INDEX idx_post_view_author_status
    ON post_view (author_id, status);

CREATE INDEX idx_post_view_category_status
    ON post_view (category_id, status);

-- ---------------------------------
-- POST-TAG RELATION TABLE
-- ---------------------------------
CREATE TABLE post_view_tags (
    post_id VARCHAR(100) NOT NULL,
    tag_id  VARCHAR(100) NOT NULL,
    PRIMARY KEY (post_id, tag_id),
    CONSTRAINT fk_pvt_post
        FOREIGN KEY (post_id)
        REFERENCES post_view(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_pvt_tag
        FOREIGN KEY (tag_id)
        REFERENCES tag_view(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE INDEX idx_pvt_tag ON post_view_tags(tag_id);
CREATE INDEX idx_pvt_post ON post_view_tags(post_id);

-- ---------------------------------
-- AUTHOR PROFILE VIEW
-- ---------------------------------
CREATE TABLE author_profile_view (
    author_id               VARCHAR(100) PRIMARY KEY,
    bio_markdown            TEXT,
    avatar_url              TEXT,
    resume_url              TEXT,
    portfolio_url           TEXT,
    social_links            JSONB,
    created_at              TIMESTAMPTZ NOT NULL,
    updated_at              TIMESTAMPTZ,
    last_version_applied    INT NOT NULL DEFAULT 0
);

-- ENUM consistency checks
-- ---------------------------------
ALTER TABLE category_view ADD CONSTRAINT chk_category_status CHECK (status IN ('ACTIVE','ARCHIVED'));
ALTER TABLE tag_view      ADD CONSTRAINT chk_tag_status CHECK (status IN ('ACTIVE','ARCHIVED'));
ALTER TABLE post_view     ADD CONSTRAINT chk_post_status CHECK (status IN ('DRAFT','PUBLISHED','ARCHIVED'));

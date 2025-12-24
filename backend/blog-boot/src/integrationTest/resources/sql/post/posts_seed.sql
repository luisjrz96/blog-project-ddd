-- TODO: add domain_events records

INSERT INTO category_view (
  id, name, slug, default_image, status,
  created_at, updated_at, archived_at, last_version_applied
) VALUES
  (
    'f371dd2f-875e-44fe-9aa7-d74a75259da6',
    'Programming',
    'programming',
    'https://picsum.photos/seed/programming/1200/601.jpg',
    'ACTIVE',
    TIMESTAMPTZ '2025-01-01 00:00:00+00',
    TIMESTAMPTZ '2025-01-01 00:00:00+00',
    NULL,
    1
  );

 INSERT INTO tag_view (
   id, name, slug, status,
   created_at, updated_at, archived_at, last_version_applied
 ) VALUES
 (
   '7e6f6b8a-2c3d-4b4a-9f9c-1a2b3c4d5e6f',
   'Java',
   'java',
   'ACTIVE',
   TIMESTAMPTZ '2025-01-01 00:00:00+00',
   TIMESTAMPTZ '2025-01-01 00:00:00+00',
   NULL,
   1
 );

 INSERT INTO post_view (id, author_id, title, slug, summary, body_markdown, cover_image,
     category_id, status, created_at, updated_at, published_at, archived_at, last_version_applied
 ) VALUES (
    'df7118ec-d81f-453b-b57f-130b38b388a9',
    'ce76ed46-d92b-43e5-8170-f64557bb1e7a',
    'Java Lambdas',
    'java-lambdas',
    'Learning Java Lambdas',
    'Java Lambdas were introduced in Java 8 ...',
    'https://postimages/image1.jpg',
    'f371dd2f-875e-44fe-9aa7-d74a75259da6',
    'PUBLISHED',
    TIMESTAMPTZ '2025-01-01 00:00:00+00',
    TIMESTAMPTZ '2025-01-01 00:00:00+00',
    TIMESTAMPTZ '2025-01-01 00:00:00+00',
    NULL,
    2
 );

INSERT INTO post_view_tags (post_id, tag_id
) VALUES (
    'df7118ec-d81f-453b-b57f-130b38b388a9',
    '7e6f6b8a-2c3d-4b4a-9f9c-1a2b3c4d5e6f'
);
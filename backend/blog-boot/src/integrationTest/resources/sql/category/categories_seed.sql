INSERT INTO domain_events (
  aggregate_type, aggregate_id, version, event_type, event_payload
) VALUES (
  'Category',
  'f371dd2f-875e-44fe-9aa7-d74a75259da6',
  1,
  'CategoryCreated',
  '{
     "id": {
       "value": "f371dd2f-875e-44fe-9aa7-d74a75259da6"
     },
     "name": {
       "value": "Programming"
     },
     "slug": {
       "value": "programming"
     },
     "createdAt": "2025-01-01T00:00:00.000000000Z",
     "defaultImage": {
       "value": "https://picsum.photos/seed/programming/1200/601.jpg"
     }
   }'::jsonb
),
(
  'Category',
  '4641a729-e5f2-45d9-bace-7a6bc696929d',
  1,
  'CategoryCreated',
  '{
     "id": {
       "value": "4641a729-e5f2-45d9-bace-7a6bc696929d"
     },
     "name": {
       "value": "DevOps"
     },
     "slug": {
       "value": "devops"
     },
     "createdAt": "2025-01-01T00:00:00.000000000Z",
     "defaultImage": {
       "value": "https://picsum.photos/seed/devops/1200/602.png"
     }
   }'::jsonb
),
(
  'Category',
  '67c68122-c3d5-4bfe-9d6b-47937b43f4b9',
  1,
  'CategoryCreated',
  '{
     "id": {
       "value": "67c68122-c3d5-4bfe-9d6b-47937b43f4b9"
     },
     "name": {
       "value": "Architecture"
     },
     "slug": {
       "value": "architecture"
     },
     "createdAt": "2025-01-01T00:00:00.000000000Z",
     "defaultImage": {
       "value": "https://picsum.photos/seed/devops/1200/603.jpg"
     }
   }'::jsonb
);

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
  ),
  (
    '4641a729-e5f2-45d9-bace-7a6bc696929d',
    'DevOps',
    'devops',
    'https://picsum.photos/seed/devops/1200/602.png',
    'ACTIVE',
    TIMESTAMPTZ '2025-01-01 00:00:00+00',
    TIMESTAMPTZ '2025-01-01 00:00:00+00',
    NULL,
    1
  ),
  (
    '67c68122-c3d5-4bfe-9d6b-47937b43f4b9',
    'Architecture',
    'architecture',
    'https://picsum.photos/seed/devops/1200/603.jpg',
    'ACTIVE',
    TIMESTAMPTZ '2025-01-01 00:00:00+00',
    TIMESTAMPTZ '2025-01-01 00:00:00+00',
    NULL,
    1
  )
ON CONFLICT (id) DO NOTHING;
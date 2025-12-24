INSERT INTO domain_events (
  aggregate_type, aggregate_id, version, event_type, event_payload
) VALUES (
  'Tag',
  '7e6f6b8a-2c3d-4b4a-9f9c-1a2b3c4d5e6f',
  1,
  'TagCreated',
  '{
     "id": {
       "value": "7e6f6b8a-2c3d-4b4a-9f9c-1a2b3c4d5e6f"
     },
     "name": {
       "value": "Java"
     },
     "slug": {
       "value": "java"
     },
     "createdAt": "2025-12-23T21:34:23.982710583Z"
   }'::jsonb
),
(
  'Tag',
  '2f1c0d9e-8b7a-4c3d-9e1f-0a1b2c3d4e5f',
  1,
  'TagCreated',
  '{
     "id": {
       "value": "2f1c0d9e-8b7a-4c3d-9e1f-0a1b2c3d4e5f"
     },
     "name": {
       "value": "Spring Boot"
     },
     "slug": {
       "value": "spring-boot"
     },
     "createdAt": "2025-12-23T21:34:23.982710583Z"
   }'::jsonb
),
(
  'Tag',
  '9a8b7c6d-5e4f-4a3b-8c7d-6e5f4a3b2c1d',
  1,
  'TagCreated',
  '{
     "id": {
       "value": "9a8b7c6d-5e4f-4a3b-8c7d-6e5f4a3b2c1d"
     },
     "name": {
       "value": "Kafka"
     },
     "slug": {
       "value": "kafka"
     },
     "createdAt": "2025-12-23T21:34:23.982710583Z"
   }'::jsonb
),
(
  'Tag',
  '0b1c2d3e-4f5a-4b6c-8d7e-9f0a1b2c3d4e',
  1,
  'TagCreated',
  '{
     "id": {
       "value": "0b1c2d3e-4f5a-4b6c-8d7e-9f0a1b2c3d4e"
     },
     "name": {
       "value": "Observability"
     },
     "slug": {
       "value": "observability"
     },
     "createdAt": "2025-12-23T21:34:23.982710583Z"
   }'::jsonb
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
),
(
  '2f1c0d9e-8b7a-4c3d-9e1f-0a1b2c3d4e5f',
  'Spring Boot',
  'spring-boot',
  'ACTIVE',
  TIMESTAMPTZ '2025-01-01 00:00:00+00',
  TIMESTAMPTZ '2025-01-01 00:00:00+00',
  NULL,
  1
),
(
  '9a8b7c6d-5e4f-4a3b-8c7d-6e5f4a3b2c1d',
  'Kafka',
  'kafka',
  'ACTIVE',
  TIMESTAMPTZ '2025-01-01 00:00:00+00',
  TIMESTAMPTZ '2025-01-01 00:00:00+00',
  NULL,
  1
),
(
  '0b1c2d3e-4f5a-4b6c-8d7e-9f0a1b2c3d4e',
  'Observability',
  'observability',
  'ACTIVE',
  TIMESTAMPTZ '2025-01-01 00:00:00+00',
  TIMESTAMPTZ '2025-01-01 00:00:00+00',
  NULL,
  1
)
ON CONFLICT (id) DO NOTHING;

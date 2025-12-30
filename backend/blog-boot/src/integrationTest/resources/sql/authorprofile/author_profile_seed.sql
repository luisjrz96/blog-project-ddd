-- ===============================
-- Author Profile Seed
-- domain_events + author_profile_view
-- ===============================

-- 1) Event (write model)
INSERT INTO domain_events (
  aggregate_type, aggregate_id, version, event_type, event_payload
) VALUES (
  'AuthorProfile',
  'ce76ed46-d92b-43e5-8170-f64557bb1e7a',
  1,
  'AuthorProfileCreated',
  '{
     "authorId": { "value": "ce76ed46-d92b-43e5-8170-f64557bb1e7a" },
     "bio": { "value": "Hello, I''m John. I like DDD and event sourcing." },
     "avatar": { "value": "https://cdn.example.com/authors/john.png" },
     "resumeUrl": { "value": "https://cdn.example.com/authors/john-resume.pdf" },
     "portfolioUrl": { "value": "https://john.dev" },
     "socialLinks": [
       { "socialNetwork": "GITHUB", "url": { "value": "https://github.com/john" } },
       { "socialNetwork": "LINKEDIN", "url": { "value": "https://linkedin.com/in/john" } }
     ],
     "createdAt": "2025-01-01T00:00:00.000000000Z"
   }'::jsonb
);

-- 2) Projection (read model)
INSERT INTO author_profile_view (
  author_id, bio_markdown, avatar_url, resume_url, portfolio_url,
  social_links, created_at, updated_at, last_version_applied
) VALUES (
  'ce76ed46-d92b-43e5-8170-f64557bb1e7a',
  'Hello, I''m John. I like DDD and event sourcing.',
  'https://cdn.example.com/authors/john.png',
  'https://cdn.example.com/authors/john-resume.pdf',
  'https://john.dev',
  '[
     {"socialNetwork":"GITHUB","url": {"value": "https://github.com/john"}},
     {"socialNetwork":"LINKEDIN","url": {"value": "https://linkedin.com/in/john"}}
   ]'::jsonb,
  TIMESTAMPTZ '2025-01-01 00:00:00+00',
  TIMESTAMPTZ '2025-01-01 00:00:00+00',
  1
)
ON CONFLICT (author_id) DO NOTHING;

SELECT 'CREATE DATABASE blog'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'blog')\gexec
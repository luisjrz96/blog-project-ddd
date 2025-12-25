DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'bloguser') THEN
    CREATE ROLE bloguser LOGIN PASSWORD 'blogpass';
  ELSE
    ALTER ROLE bloguser WITH LOGIN PASSWORD 'blogpass';
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'keycloakuser') THEN
    CREATE ROLE keycloakuser LOGIN PASSWORD 'keycloakpass';
  ELSE
    ALTER ROLE keycloakuser WITH LOGIN PASSWORD 'keycloakpass';
  END IF;
END $$;

SELECT format('CREATE DATABASE %I OWNER %I', 'blog', 'bloguser')
WHERE NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'blog')
\gexec

SELECT format('CREATE DATABASE %I OWNER %I', 'keycloak', 'keycloakuser')
WHERE NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'keycloak')
\gexec

GRANT ALL PRIVILEGES ON DATABASE blog TO bloguser;
GRANT ALL PRIVILEGES ON DATABASE keycloak TO keycloakuser;

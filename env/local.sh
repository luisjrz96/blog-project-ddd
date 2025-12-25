# docker env variables - THIS IS ONLY FOR TESTING
# secrets could be managed with tools like k8s secrets or vault in prod
# keycloak container env variables
# if you want to change the values also change info in the file:
# docker/init/postgres/01-create-db.sql
export KC_DB_USERNAME=keycloakuser
export KC_DB_PASSWORD=keycloakpass
# postgres container env variables
export DEFAULT_DB=defaultdb
export POSTGRES_USER=defualtuser
export POSTGRES_PASSWORD=defaultpass
# pgadmin container env variables
export PGADMIN_DEFAULT_EMAIL=admin@admin.com
export PGADMIN_DEFAULT_PASSWORD=admin
# grafana container env variables
export GF_SECURITY_ADMIN_USER=gfuser
export GF_SECURITY_ADMIN_PASSWORD=gfadmin
# app env variables
# if you want to run your application locally you will need to change:
# postgres hostname to localhost in env variable SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/blog
export SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI=http://auth.localtest.me:8081/realms/blogrealm
export SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/blog
export SPRING_DATASOURCE_USERNAME=bloguser
export SPRING_DATASOURCE_PASSWORD=blogpass
export SPRING_PROFILES_ACTIVE=local
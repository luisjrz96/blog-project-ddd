package com.luisjrz96.blog.it;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Testcontainers
public abstract class IntegrationTestBase {

  private static final HttpClient HTTP = HttpClient.newHttpClient();
  private static final ObjectMapper JSON = new ObjectMapper();

  protected IntegrationTestBase() {}

  @SuppressWarnings("resource")
  static final PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:16")
          .withDatabaseName("blog")
          .withUsername("bloguser")
          .withPassword("blogpass");

  @SuppressWarnings("resource")
  static final GenericContainer<?> keycloak =
      new GenericContainer<>("quay.io/keycloak/keycloak:26.0")
          .withExposedPorts(8080)
          .withEnv("KEYCLOAK_ADMIN", "admin")
          .withEnv("KEYCLOAK_ADMIN_PASSWORD", "admin")
          .withEnv("KC_HTTP_ENABLED", "true")
          .withEnv("KC_HOSTNAME_STRICT", "false")
          .withCopyFileToContainer(
              MountableFile.forClasspathResource("keycloak/blogrealm-realm.json"),
              "/opt/keycloak/data/import/blogrealm-realm.json")
          .withCommand("start-dev", "--http-port=8080", "--import-realm")
          .waitingFor(
              Wait.forHttp("/realms/blogrealm/.well-known/openid-configuration")
                  .forStatusCode(200)
                  .withStartupTimeout(Duration.ofMinutes(2)));

  @BeforeAll
  static void startContainers() {
    postgres.start();
    keycloak.start();
  }

  @SuppressWarnings("unused")
  @DynamicPropertySource
  static void registerProps(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);

    registry.add(
        "spring.security.oauth2.resourceserver.jwt.issuer-uri",
        () ->
            "http://"
                + keycloak.getHost()
                + ":"
                + keycloak.getMappedPort(8080)
                + "/realms/blogrealm");
  }

  @SuppressWarnings("java:S112")
  protected String obtainAdminAccessToken() throws Exception {
    String tokenUrl =
        "http://"
            + keycloak.getHost()
            + ":"
            + keycloak.getMappedPort(8080)
            + "/realms/blogrealm/protocol/openid-connect/token";

    String form =
        "grant_type=password"
            + "&client_id="
            + enc("blog-api")
            + "&username="
            + enc("luis")
            + "&password="
            + enc("luispass");

    HttpResponse<String> res =
        send(
            HttpRequest.newBuilder()
                .uri(URI.create(tokenUrl))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build());

    assertThat(res.statusCode())
        .withFailMessage("Token endpoint failed: %s Body: %s", res.statusCode(), res.body())
        .isEqualTo(200);

    return extractAccessToken(res.body());
  }

  protected static HttpResponse<String> send(HttpRequest req) throws IOException {
    try {
      return HTTP.send(req, HttpResponse.BodyHandlers.ofString());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException("HTTP request interrupted", e);
    }
  }

  protected static String extractAccessToken(String json) throws IOException {
    JsonNode root = JSON.readTree(json);
    JsonNode token = root.get("access_token");
    if (token == null || token.asText().isBlank()) {
      throw new IllegalStateException("No access_token in response: " + json);
    }
    return token.asText();
  }

  protected static String enc(String v) {
    return URLEncoder.encode(v, StandardCharsets.UTF_8);
  }

  protected static JsonNode readJsonFromClasspath(String path) throws Exception {
    var resource = new ClassPathResource(path);
    try (var in = resource.getInputStream()) {
      return JSON.readTree(in);
    }
  }

  protected static JsonNode convertStringToJsonNode(String body) throws JsonProcessingException {
    return JSON.readTree(body);
  }
}

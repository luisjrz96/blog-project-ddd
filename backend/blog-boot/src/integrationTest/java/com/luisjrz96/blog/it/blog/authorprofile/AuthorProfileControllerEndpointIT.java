package com.luisjrz96.blog.it.blog.authorprofile;

import static com.luisjrz96.blog.it.Util.getApiEndpoint;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import com.fasterxml.jackson.databind.JsonNode;
import com.luisjrz96.blog.it.IntegrationTestBase;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthorProfileControllerEndpointIT extends IntegrationTestBase {

  private static final String AUTHOR_PATH_PREFIX = "/api/authors";

  @LocalServerPort int port;

  @SuppressWarnings("java:S6813")
  @Autowired
  JdbcTemplate jdbc;

  @Sql(
      scripts = "/sql/authorprofile/author_profile_seed.sql",
      executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  @Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  @Test
  void queryAuthorProfileByIdShouldReturn200() throws IOException {
    String authorId = "ce76ed46-d92b-43e5-8170-f64557bb1e7a";

    var res =
        send(
            HttpRequest.newBuilder()
                .uri(URI.create(getApiEndpoint(port, AUTHOR_PATH_PREFIX, "/" + authorId)))
                .GET()
                .build());

    assertThat(res.statusCode())
        .withFailMessage("Expected 2xx but got %s. Body: %s", res.statusCode(), res.body())
        .isEqualTo(200);

    JsonNode actual = convertStringToJsonNode(res.body());
    JsonNode expected =
        readJsonFromClasspath("api/responses/authorprofile/authorProfileByIdResponse.json");

    assertThat(actual).isEqualTo(expected);
  }

  @Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  @Test
  void adminCreateAuthorProfileShouldReturn201AndPersistEventAndView() throws Exception {
    String token = obtainAdminAccessToken();

    String body =
        readJsonFromClasspath("api/requests/authorprofile/createAuthorProfileRequest.json")
            .toString();

    var res =
        send(
            HttpRequest.newBuilder()
                .uri(URI.create(getApiEndpoint(port, AUTHOR_PATH_PREFIX, null)))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build());

    assertThat(res.statusCode())
        .withFailMessage("Expected 201 but got %s. Body: %s", res.statusCode(), res.body())
        .isEqualTo(201);

    String authorId =
        jdbc.queryForObject("select author_id from author_profile_view limit 1", String.class);
    assertThat(authorId).isNotNull();

    Integer events =
        jdbc.queryForObject(
            "select count(*) from domain_events_author_profile where aggregate_id = ?",
            Integer.class,
            authorId);
    assertThat(events).isNotNull();
    assertThat(events).isEqualTo(1);
  }

  @Sql(
      scripts = "/sql/authorprofile/author_profile_seed.sql",
      executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  @Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  @Test
  void adminUpdateAuthorProfileShouldReturn204AndUpdateViewAndAppendEvent() throws Exception {
    String token = obtainAdminAccessToken();

    String body =
        readJsonFromClasspath("api/requests/authorprofile/updateAuthorProfileRequest.json")
            .toString();

    var res =
        send(
            HttpRequest.newBuilder()
                .uri(URI.create(getApiEndpoint(port, AUTHOR_PATH_PREFIX, null)))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build());

    assertThat(res.statusCode())
        .withFailMessage("Expected 204 but got %s. Body: %s", res.statusCode(), res.body())
        .isEqualTo(204);

    String authorId =
        jdbc.queryForObject("select author_id from author_profile_view limit 1", String.class);

    String bio =
        jdbc.queryForObject(
            "select bio_markdown from author_profile_view where author_id = ?",
            String.class,
            authorId);
    assertThat(bio).contains("updated");

    Integer events =
        jdbc.queryForObject(
            "select count(*) from domain_events_author_profile where aggregate_id = ?",
            Integer.class,
            authorId);
    assertThat(events).isNotNull();
    assertThat(events).isGreaterThanOrEqualTo(2);
  }
}

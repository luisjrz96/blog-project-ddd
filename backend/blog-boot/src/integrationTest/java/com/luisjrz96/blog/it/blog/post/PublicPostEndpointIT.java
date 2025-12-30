package com.luisjrz96.blog.it.blog.post;

import static com.luisjrz96.blog.it.Util.getApiEndpoint;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

import com.fasterxml.jackson.databind.JsonNode;
import com.luisjrz96.blog.it.IntegrationTestBase;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PublicPostEndpointIT extends IntegrationTestBase {

  private static final String POST_PATH = "/api/posts";

  @LocalServerPort int port;

  @Sql(scripts = "/sql/post/posts_seed.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  @Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  @Test
  void queryPostsPageShouldReturn200() throws IOException {
    var res =
        send(
            HttpRequest.newBuilder()
                .uri(URI.create(getApiEndpoint(port, POST_PATH, null)))
                .GET()
                .build());

    assertThat(res.statusCode())
        .withFailMessage("Expected 2xx but got %s. Body: %s", res.statusCode(), res.body())
        .isBetween(200, 299);

    JsonNode actual = convertStringToJsonNode(res.body());
    JsonNode expected = readJsonFromClasspath("api/responses/post/postsPageResponse.json");

    assertThat(actual).isEqualTo(expected);
  }

  @Sql(scripts = "/sql/post/posts_seed.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  @Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  @Test
  void queryPostByIdShouldReturn200() throws IOException {
    String id = "df7118ec-d81f-453b-b57f-130b38b388a9";

    var res =
        send(
            HttpRequest.newBuilder()
                .uri(URI.create(getApiEndpoint(port, POST_PATH, "/" + id)))
                .GET()
                .build());

    assertThat(res.statusCode())
        .withFailMessage("Expected 2xx but got %s. Body: %s", res.statusCode(), res.body())
        .isBetween(200, 299);

    JsonNode actual = convertStringToJsonNode(res.body());
    JsonNode expected = readJsonFromClasspath("api/responses/post/postByIdResponse.json");

    assertThat(actual).isEqualTo(expected);
  }
}

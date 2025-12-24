package com.luisjrz96.blog.it.blog.tag;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.net.URI;
import java.net.http.HttpRequest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

import com.fasterxml.jackson.databind.JsonNode;
import com.luisjrz96.blog.it.IntegrationTestBase;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PublicTagEndpointIT extends IntegrationTestBase {

  @LocalServerPort int port;

  @Sql(scripts = "/sql/tag/tags_seed.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  @Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  @Test
  void queryCategoriesPageShouldReturn200() throws Exception {
    var res =
        send(
            HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/tags"))
                .GET()
                .build());

    assertThat(res.statusCode())
        .withFailMessage("Expected 2xx but got %s. Body: %s", res.statusCode(), res.body())
        .isBetween(200, 299);

    JsonNode actual = convertStringToJsonNode(res.body());
    JsonNode expected = readJsonFromClasspath("api/responses/tag/tagsPageResponse.json");

    assertThat(actual).isEqualTo(expected);
  }

  @Sql(scripts = "/sql/tag/tags_seed.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  @Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  @Test
  void queryCategoryByIdShouldReturn200() throws Exception {
    String id = "9a8b7c6d-5e4f-4a3b-8c7d-6e5f4a3b2c1d";

    var res =
        send(
            HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/tags/" + id))
                .GET()
                .build());

    assertThat(res.statusCode())
        .withFailMessage("Expected 2xx but got %s. Body: %s", res.statusCode(), res.body())
        .isBetween(200, 299);

    JsonNode actual = convertStringToJsonNode(res.body());
    JsonNode expected = readJsonFromClasspath("api/responses/tag/tagByIdResponse.json");

    assertThat(actual).isEqualTo(expected);
  }
}

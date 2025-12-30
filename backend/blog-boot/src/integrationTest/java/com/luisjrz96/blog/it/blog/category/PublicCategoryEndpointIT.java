package com.luisjrz96.blog.it.blog.category;

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
public class PublicCategoryEndpointIT extends IntegrationTestBase {

  private static final String CATEGORY_PATH = "/api/categories";

  @LocalServerPort int port;

  @Sql(
      scripts = "/sql/category/categories_seed.sql",
      executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  @Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  @Test
  void queryCategoriesPageShouldReturn200() throws IOException {
    var res =
        send(
            HttpRequest.newBuilder()
                .uri(URI.create(getApiEndpoint(port, CATEGORY_PATH, null)))
                .GET()
                .build());

    assertThat(res.statusCode())
        .withFailMessage("Expected 2xx but got %s. Body: %s", res.statusCode(), res.body())
        .isBetween(200, 299);

    JsonNode actual = convertStringToJsonNode(res.body());
    JsonNode expected = readJsonFromClasspath("api/responses/category/categoriesPageResponse.json");

    assertThat(actual).isEqualTo(expected);
  }

  @Sql(
      scripts = "/sql/category/categories_seed.sql",
      executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  @Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  @Test
  void queryCategoryByIdShouldReturn200() throws IOException {
    String id = "f371dd2f-875e-44fe-9aa7-d74a75259da6";

    var res =
        send(
            HttpRequest.newBuilder()
                .uri(URI.create(getApiEndpoint(port, CATEGORY_PATH, "/" + id)))
                .GET()
                .build());

    assertThat(res.statusCode())
        .withFailMessage("Expected 2xx but got %s. Body: %s", res.statusCode(), res.body())
        .isBetween(200, 299);

    JsonNode actual = convertStringToJsonNode(res.body());
    JsonNode expected = readJsonFromClasspath("api/responses/category/categoryByIdResponse.json");

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void queryCategoryByIdShouldReturn400() throws IOException {
    String id = "a371ee2f-895e-44fe-9aa7-a74e75259fe7";

    var res =
        send(
            HttpRequest.newBuilder()
                .uri(URI.create(getApiEndpoint(port, CATEGORY_PATH, "/" + id)))
                .GET()
                .build());

    assertThat(res.statusCode())
        .withFailMessage("Expected 404 but got %s. Body: %s", res.statusCode(), res.body())
        .isEqualTo(404);
  }
}

package com.luisjrz96.blog.it.blog.post;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.luisjrz96.blog.it.IntegrationTestBase;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AdminPostEndpointIT extends IntegrationTestBase {

  @LocalServerPort int port;

  // TODO: implement tests with different data
  @Test
  void adminEndpointAllowsAdmin() throws Exception {
    String jwt = obtainAdminAccessToken();

    HttpResponse<String> res =
        send(
            HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/admin/posts?status=DRAFT"))
                .header("Authorization", "Bearer " + jwt)
                .GET()
                .build());

    assertThat(res.statusCode())
        .withFailMessage("Expected 2xx but got %s. Body: %s", res.statusCode(), res.body())
        .isBetween(200, 299);
  }
}

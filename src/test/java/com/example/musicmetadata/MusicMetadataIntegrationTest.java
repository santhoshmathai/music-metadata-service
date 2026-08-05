package com.example.musicmetadata;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MusicMetadataIntegrationTest {
    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:18-alpine");

    @DynamicPropertySource
    static void database(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @LocalServerPort int port;
    @Autowired TestRestTemplate http;

    @Test
    void supportsTheCoreCatalogueJourney() {
        ResponseEntity<JsonNode> artist = http.postForEntity(url("/api/v1/artists"),
                Map.of("name", "Daft Punk", "aliases", new String[]{"Darlin'"}), JsonNode.class);
        assertThat(artist.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String artistId = artist.getBody().get("id").asText();

        ResponseEntity<JsonNode> track = http.postForEntity(url("/api/v1/artists/" + artistId + "/tracks"),
                Map.of("title", "Around the World", "genre", "House", "durationMs", 429533), JsonNode.class);
        assertThat(track.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<JsonNode> tracks = http.getForEntity(
                url("/api/v1/artists/" + artistId + "/tracks"), JsonNode.class);
        assertThat(tracks.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(tracks.getBody().at("/content/0/title").asText()).isEqualTo("Around the World");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<JsonNode> renamed = http.exchange(url("/api/v1/artists/" + artistId), HttpMethod.PATCH,
                new HttpEntity<>(Map.of("name", "Daft Punk Robots"), headers), JsonNode.class);
        assertThat(renamed.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(renamed.getBody().get("aliases").toString()).contains("Daft Punk");

        ResponseEntity<JsonNode> featured = http.getForEntity(
                url("/api/v1/artists/artist-of-the-day"), JsonNode.class);
        assertThat(featured.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(featured.getBody().at("/artist/id").asText()).isEqualTo(artistId);
    }

    @Test
    void returnsProblemDetailsForInvalidTrack() {
        ResponseEntity<JsonNode> artist = http.postForEntity(url("/api/v1/artists"),
                Map.of("name", "Validation Artist"), JsonNode.class);
        String artistId = artist.getBody().get("id").asText();
        ResponseEntity<JsonNode> result = http.postForEntity(url("/api/v1/artists/" + artistId + "/tracks"),
                Map.of("title", "", "genre", "Rock", "durationMs", 0), JsonNode.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody().get("code").asText()).isEqualTo("VALIDATION_FAILED");
    }

    private String url(String path) { return "http://localhost:" + port + path; }
}

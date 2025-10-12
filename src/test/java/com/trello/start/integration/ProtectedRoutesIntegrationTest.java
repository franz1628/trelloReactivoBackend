package com.trello.start.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.trello.start.config.JwtUtils;
import com.trello.start.dto.RegisterRequest;
import com.trello.start.model.User;
import com.trello.start.repository.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class ProtectedRoutesIntegrationTest {

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:6.0");

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JwtUtils jwtUtil;

    @Autowired
    private UserRepository userRepository;

    private String validToken;

    @BeforeEach
    void setup() {
        userRepository.deleteAll().block();
        User newUser = new User();
        var request = new RegisterRequest();
        request.setEmail("gualambo@gmail.com");
        request.setPassword("123456");
        request.setName("Franz");
        request.setUsername("Gualambo");

        User user = userRepository.save(newUser).block();
        validToken = jwtUtil.generateToken(user);
    }

    @Test
    void accessProtected_withValidToken_shouldReturn200() {
        webTestClient.get()
                .uri("/api/user/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void accessProtected_withoutToken_shouldReturn401() {
        webTestClient.get()
                .uri("/api/user/me")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void accessProtected_withInvalidToken_shouldReturn401() {
        webTestClient.get()
                .uri("/api/user/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token")
                .exchange()
                .expectStatus().isUnauthorized();
    }
}

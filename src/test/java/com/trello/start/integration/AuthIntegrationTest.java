package com.trello.start.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.trello.start.dto.RegisterRequest;
import com.trello.start.dto.RequestLogin;
import com.trello.start.repository.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class AuthIntegrationTest {
    
    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void setProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    }

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanUp() {
        userRepository.deleteAll().block();
    }

    @Test
    void register_withValidData_shouldReturn201() {
        var request = new RegisterRequest();
        request.setEmail("gualambo99@gmail.com");
        request.setPassword("password123");
        request.setName("John Doe");
        request.setUsername("johndoe");

        webTestClient.post()
                .uri("api/auth/register")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").exists();
    }

    @Test
    void register_withMissingEmail_shouldReturn400() {
        var request = new RegisterRequest();
        request.setPassword("password123");
        request.setName("John Doe");
        request.setUsername("johndoe");

        webTestClient.post()
                .uri("api/auth/register")
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void register_withDuplicatedEmail_shouldReturn400() {
        var request = new RegisterRequest();
        request.setEmail("gualambo2@gmail.com");
        request.setPassword("password123");
        request.setName("John Doe");
        request.setUsername("johndoe");

        webTestClient.post()
                .uri("api/auth/register")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated();

        webTestClient.post()
            .uri("api/auth/register")
            .bodyValue(request)
            .exchange()
            .expectStatus().isEqualTo(409);
    }

    @Test
    void login_withValidCredentials_shouldReturn200() {

        var request = new RegisterRequest();
        request.setEmail("gualambo2@gmail.com");
        request.setPassword("password123");
        request.setName("John Doe");
        request.setUsername("johndoe");

        webTestClient.post()
                .uri("api/auth/register")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated();

        var requestLogin = new RequestLogin();
        requestLogin.setEmail("gualambo2@gmail.com");
        requestLogin.setPassword("password123");

        webTestClient.post()
                .uri("api/auth/login")
                .bodyValue(requestLogin)
                .exchange()
                .expectStatus().isEqualTo(200);
    }

    @Test
    void login_withInvalidCredentials_shouldReturn401() {

        var request = new RegisterRequest();
        request.setEmail("gualambo2@gmail.com");
        request.setPassword("password123");
        request.setName("John Doe");
        request.setUsername("johndoe");

        webTestClient.post()
                .uri("api/auth/register")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated();

        var requestLogin = new RequestLogin();
        requestLogin.setEmail("gualambo2@gmail.com");
        requestLogin.setPassword("wrongpassword");

        webTestClient.post()
                .uri("api/auth/login")
                .bodyValue(requestLogin)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void login_withUnregisteredEmail_shouldReturn404() {

        var request = new RequestLogin();
        request.setEmail("gualambo2@gmail.com");
        request.setPassword("password123");

        webTestClient.post()
                .uri("api/auth/login")
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound();
    }
}

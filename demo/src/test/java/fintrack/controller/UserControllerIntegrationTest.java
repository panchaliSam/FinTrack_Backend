package fintrack.controller;//package com.fintrack.controller;

import com.fintrack.FinTrackApplication;
import com.fintrack.dto.UserDto;
import com.fintrack.entity.User;
import com.fintrack.service.UserService;
import com.fintrack.type.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = FinTrackApplication.class
)
@Import(UserControllerIntegrationTest.TestConfig.class)
public class UserControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserService userService;

    private User mockUser;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/user";

        mockUser = new User("65f5c8a4e9b1d3b6f0a8f7e2", "John", "Doe", "john@example.com", "password123", Role.REGULAR_USER);

        Mockito.when(userService.getAllUsers()).thenReturn(List.of(mockUser));
        Mockito.when(userService.getUserById("65f5c8a4e9b1d3b6f0a8f7e2")).thenReturn(Optional.of(mockUser));
        Mockito.when(userService.authenticateUser("john@example.com", "password123")).thenReturn("mock-token");
        Mockito.when(userService.createUser(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(mockUser);
    }

    @Test
    void testGetAllUsers() {
        ResponseEntity<User[]> response = restTemplate.getForEntity(baseUrl, User[].class);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("john@example.com", response.getBody()[0].getEmail());
    }

    @Test
    void testGetUserById() {
        ResponseEntity<User> response = restTemplate.getForEntity(baseUrl + "/65f5c8a4e9b1d3b6f0a8f7e2", User.class);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("john@example.com", response.getBody().getEmail());
    }

    @Test
    void testSignUp() {
        UserDto newUser = new UserDto("Jane", "Doe", "jane@example.com", "password456", Role.ADMIN);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<UserDto> request = new HttpEntity<>(newUser, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/sign-up", request, String.class);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User registered successfully", response.getBody());
    }

    @Test
    void testSignIn() {
        Map<String, String> loginRequest = Map.of("email", "john@example.com", "password", "password123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(loginRequest, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(baseUrl + "/sign-in", request, Map.class);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("mock-token", response.getBody().get("token"));
    }

    @Configuration
    static class TestConfig {
        @Bean
        public UserService userService() {
            return mock(UserService.class);
        }
    }
}
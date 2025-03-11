package fintrack.controller;//package com.fintrack.controller;

import com.fintrack.FinTrackApplication;
import com.fintrack.dto.GoalDto;
import com.fintrack.entity.Goal;
import com.fintrack.service.GoalService;
import org.bson.types.ObjectId;
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
import org.springframework.context.annotation.Import;
import org.springframework.http.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.springframework.test.context.junit.jupiter.SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = FinTrackApplication.class
)
@Import(GoalControllerIntegrationTest.TestConfig.class)
public class GoalControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private GoalService goalService;

    private Goal mockGoal;
    private String baseUrl;
    private String mockGoalId;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/goals";
        mockGoalId = new ObjectId().toHexString();

        mockGoal = new Goal(
                mockGoalId,
                new ObjectId().toHexString(),
                "Buy a car",
                20000.0,
                5000.0,
                LocalDateTime.of(2025, 12, 31, 0, 0),
                500.0
        );

        Mockito.when(goalService.getAllGoals()).thenReturn(List.of(mockGoal));
        Mockito.when(goalService.getGoalById(mockGoalId)).thenReturn(Optional.of(mockGoal));
        Mockito.when(goalService.createGoal(any(), any(), anyDouble(), anyDouble(), any(), anyDouble()))
                .thenReturn(mockGoal);
        Mockito.when(goalService.updateGoal(eq(mockGoalId), any(), any(), anyDouble(), anyDouble(), any(), anyDouble()))
                .thenReturn(Optional.of(mockGoal));
        Mockito.when(goalService.deleteGoal(mockGoalId)).thenReturn(true);
        Mockito.when(goalService.getMonthlyGoalProgress(mockGoalId)).thenReturn(25L);
    }

    @Test
    void testGetMonthlyGoalProgress() {
        ResponseEntity<Long> response = restTemplate.getForEntity(baseUrl + "/" + mockGoalId + "/monthly-progress", Long.class);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(25L, response.getBody());
    }

    @Test
    void testGetAllGoals() {
        ResponseEntity<Goal[]> response = restTemplate.getForEntity(baseUrl, Goal[].class);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Buy a car", response.getBody()[0].getGoalName());
    }

    @Test
    void testGetGoalById() {
        ResponseEntity<Goal> response = restTemplate.getForEntity(baseUrl + "/" + mockGoalId, Goal.class);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Buy a car", response.getBody().getGoalName());
    }

    @Test
    void testCreateGoal() {
        GoalDto newGoal = new GoalDto(
                new ObjectId().toHexString(),
                "Vacation",
                5000.0,
                1000.0,
                LocalDateTime.of(2026, 6, 1, 0, 0),
                200.0
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GoalDto> request = new HttpEntity<>(newGoal, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Goal created successfully", response.getBody());
    }

    @Test
    void testUpdateGoal() {
        GoalDto updatedGoal = new GoalDto(
                new ObjectId().toHexString(),
                "New Car",
                25000.0,
                6000.0,
                LocalDateTime.of(2026, 12, 31, 0, 0),
                600.0
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GoalDto> request = new HttpEntity<>(updatedGoal, headers);
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/" + mockGoalId, HttpMethod.PUT, request, String.class);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Goal updated successfully", response.getBody());
    }

    @Test
    void testDeleteGoal() {
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/" + mockGoalId, HttpMethod.DELETE, null, String.class);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testDeleteGoalNotFound() {
        String invalidId = new ObjectId().toHexString();
        when(goalService.deleteGoal(invalidId)).thenReturn(false);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/" + invalidId, HttpMethod.DELETE, null, String.class);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Configuration
    static class TestConfig {
        @Bean
        public GoalService goalService() {
            return mock(GoalService.class);
        }
    }
}
package fintrack.controller;//package com.fintrack.controller;

import com.fintrack.FinTrackApplication;
import com.fintrack.dto.SavingsDto;
import com.fintrack.entity.Savings;
import com.fintrack.service.GoalService;
import com.fintrack.service.SavingsService;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.springframework.test.context.junit.jupiter.SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = FinTrackApplication.class
)
@Import(SavingsControllerIntegrationTest.TestConfig.class)
public class SavingsControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private GoalService goalService;

    @Autowired
    private SavingsService savingsService;

    private Savings mockSavings;
    private String baseUrl;
    private String mockSavingsId;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/savings";
        mockSavingsId = new ObjectId().toHexString();

        mockSavings = new Savings(
                mockSavingsId,
                5000.0
        );

        Mockito.when(savingsService.getAllSavings()).thenReturn(List.of(mockSavings));
        Mockito.when(savingsService.updateSavings(eq(mockSavingsId), anyDouble()))
                .thenReturn(Optional.of(mockSavings));
        Mockito.when(goalService.getTotalSavings()).thenReturn(10000.0);
    }

    @Test
    void testGetTotalSavings() {
        ResponseEntity<Double> response = restTemplate.getForEntity(baseUrl + "/total-savings", Double.class);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(10000.0, response.getBody());
    }

    @Test
    void testGetAllSavings() {
        ResponseEntity<Savings[]> response = restTemplate.getForEntity(baseUrl, Savings[].class);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(5000.0, response.getBody()[0].getTotalAmount());
    }

    @Test
    void testUpdateSavings() {
        SavingsDto updatedSavings = new SavingsDto(
                mockSavingsId,
                7000.0
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SavingsDto> request = new HttpEntity<>(updatedSavings, headers);
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/" + mockSavingsId, HttpMethod.PUT, request, String.class);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Savings updated successfully", response.getBody());
    }

    @Test
    void testUpdateSavingsNotFound() {
        String invalidId = new ObjectId().toHexString();
        when(savingsService.updateSavings(eq(invalidId), anyDouble())).thenReturn(Optional.empty());

        SavingsDto updatedSavings = new SavingsDto(
                invalidId,
                8000.0
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SavingsDto> request = new HttpEntity<>(updatedSavings, headers);
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/" + invalidId, HttpMethod.PUT, request, String.class);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Configuration
    static class TestConfig {
        @Bean
        public GoalService goalService() {
            return mock(GoalService.class);
        }

        @Bean
        public SavingsService savingsService() {
            return mock(SavingsService.class);
        }
    }
}
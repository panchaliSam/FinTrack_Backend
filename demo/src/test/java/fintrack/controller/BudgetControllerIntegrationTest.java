package fintrack.controller;

import com.fintrack.FinTrackApplication;
import com.fintrack.dto.BudgetDto;
import com.fintrack.entity.Budget;
import com.fintrack.service.BudgetService;
import com.fintrack.type.BudgetStatus;
import com.fintrack.type.Category;
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
@Import(BudgetControllerIntegrationTest.TestConfig.class)
public class BudgetControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BudgetService budgetService;

    private Budget mockBudget;
    private String baseUrl;
    private String mockBudgetId;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/budget";
        mockBudgetId = new ObjectId().toHexString();

        mockBudget = new Budget(
                mockBudgetId,
                new ObjectId().toHexString(),
                Category.INCOME,
                500.0,
                LocalDateTime.of(2025, 1, 1, 0, 0),
                LocalDateTime.of(2025, 1, 31, 0, 0),
                BudgetStatus.ACTIVE
        );

        Mockito.when(budgetService.getAllBudgets()).thenReturn(List.of(mockBudget));
        Mockito.when(budgetService.getBudgetById(mockBudgetId)).thenReturn(Optional.of(mockBudget));
        Mockito.when(budgetService.createBudget(any(), any(), anyDouble(), any(), any(), any())).thenReturn(mockBudget);
        Mockito.when(budgetService.updateBudget(eq(mockBudgetId), any(), any(), anyDouble(), any(), any(), any()))
                .thenReturn(Optional.of(mockBudget));
        Mockito.when(budgetService.deleteBudget(mockBudgetId)).thenReturn(true);
    }

    @Test
    void testGetAllBudgets() {
        ResponseEntity<Budget[]> response = restTemplate.getForEntity(baseUrl, Budget[].class);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(Category.INCOME, response.getBody()[0].getBudgetCategory());
    }

    @Test
    void testGetBudgetById() {
        ResponseEntity<Budget> response = restTemplate.getForEntity(baseUrl + "/" + mockBudgetId, Budget.class);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(Category.INCOME, response.getBody().getBudgetCategory());
    }

    @Test
    void testCreateBudget() {
        BudgetDto newBudget = new BudgetDto(
                new ObjectId().toHexString(),
                Category.INCOME,
                200.0,
                LocalDateTime.of(2025, 1, 1, 0, 0, 0, 0),
                LocalDateTime.of(2026, 1, 1, 0, 0, 0, 0),
                BudgetStatus.ACTIVE
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<BudgetDto> request = new HttpEntity<>(newBudget, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Budget created successfully", response.getBody());
    }

    @Test
    void testUpdateBudget() {
        BudgetDto updatedBudget = new BudgetDto(
                new ObjectId().toHexString(),
                Category.INCOME,
                300.0,
                LocalDateTime.of(2025, 1, 1, 0, 0, 0, 0),
                LocalDateTime.of(2026, 1, 1, 0, 0, 0, 0),
                BudgetStatus.ACTIVE
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<BudgetDto> request = new HttpEntity<>(updatedBudget, headers);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/" + mockBudgetId, HttpMethod.PUT, request, String.class);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Budget updated successfully", response.getBody());
    }

    @Test
    void testDeleteBudget() {
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/" + mockBudgetId, HttpMethod.DELETE, null, String.class);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testDeleteBudgetNotFound() {
        String invalidId = new ObjectId().toHexString();
        when(budgetService.deleteBudget(invalidId)).thenReturn(false);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/" + invalidId, HttpMethod.DELETE, null, String.class);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Configuration
    static class TestConfig {
        @Bean
        public BudgetService budgetService() {
            return mock(BudgetService.class);
        }
    }
}
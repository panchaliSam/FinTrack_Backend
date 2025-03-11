package fintrack.controller;

import com.fintrack.controller.BudgetController;
import com.fintrack.dto.BudgetDto;
import com.fintrack.entity.Budget;
import com.fintrack.service.BudgetService;
import com.fintrack.type.BudgetStatus;
import com.fintrack.type.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetControllerTest {

    @Mock
    private BudgetService budgetService;

    @InjectMocks
    private BudgetController budgetController;

    private Budget mockBudget;
    private BudgetDto mockBudgetDto;

    @BeforeEach
    void setUp() {
        mockBudget = new Budget("65f8b123abc123d456e78901","67b853e12101d21c20fad9ee",
                Category.EXPENSE,
                1500.00,
                LocalDateTime.of(2025, 1, 1, 0, 0, 0, 0),
                LocalDateTime.of(2026, 1, 1, 0, 0, 0, 0),
                BudgetStatus.ACTIVE);

        mockBudgetDto = new BudgetDto(				"67b853e12101d21c20fad9ee",
                Category.EXPENSE,
                1500.00,
                LocalDateTime.of(2025, 1, 1, 0, 0, 0, 0),
                LocalDateTime.of(2026, 1, 1, 0, 0, 0, 0),
                BudgetStatus.ACTIVE);
    }

    @Test
    void createBudget() {
        when(budgetService.createBudget(
                mockBudgetDto.getUserId(),
                mockBudgetDto.getBudgetCategory(),
                mockBudgetDto.getAmount(),
                mockBudgetDto.getStartDate(),
                mockBudgetDto.getEndDate(),
                mockBudgetDto.getBudgetStatus()
        )).thenReturn(mockBudget);

        ResponseEntity<String> response = budgetController.createBudget(mockBudgetDto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Budget created successfully", response.getBody());

        verify(budgetService, times(1)).createBudget(
                mockBudgetDto.getUserId(),
                mockBudgetDto.getBudgetCategory(),
                mockBudgetDto.getAmount(),
                mockBudgetDto.getStartDate(),
                mockBudgetDto.getEndDate(),
                mockBudgetDto.getBudgetStatus()
        );
    }

    @Test
    void getAllBudgets_Success() {
        when(budgetService.getAllBudgets()).thenReturn(List.of(mockBudget));

        ResponseEntity<List<Budget>> response = budgetController.getAllBudgets();

        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());
        assertEquals(mockBudget, response.getBody().get(0));

        verify(budgetService, times(1)).getAllBudgets();
    }

    @Test
    void getAllBudgets_Empty() {
        when(budgetService.getAllBudgets()).thenReturn(List.of());

        ResponseEntity<List<Budget>> response = budgetController.getAllBudgets();

        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void getBudgetById_Found() {
        when(budgetService.getBudgetById("65f8b123abc123d456e78901")).thenReturn(Optional.of(mockBudget));

        ResponseEntity<Budget> response = budgetController.getBudgetById("65f8b123abc123d456e78901");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockBudget, response.getBody());
    }

    @Test
    void getBudgetById_NotFound() {
        when(budgetService.getBudgetById("invalidId")).thenReturn(Optional.empty());

        ResponseEntity<Budget> response = budgetController.getBudgetById("invalidId");

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void updateBudget_Success() {
        when(budgetService.updateBudget(
                "65f8b123abc123d456e78901",
                mockBudgetDto.getUserId(),
                mockBudgetDto.getBudgetCategory(),
                mockBudgetDto.getAmount(),
                mockBudgetDto.getStartDate(),
                mockBudgetDto.getEndDate(),
                mockBudgetDto.getBudgetStatus()
        )).thenReturn(Optional.of(mockBudget));

        ResponseEntity<String> response = budgetController.updateBudget("65f8b123abc123d456e78901", mockBudgetDto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Budget updated successfully", response.getBody());

        verify(budgetService, times(1)).updateBudget(
                "65f8b123abc123d456e78901",
                mockBudgetDto.getUserId(),
                mockBudgetDto.getBudgetCategory(),
                mockBudgetDto.getAmount(),
                mockBudgetDto.getStartDate(),
                mockBudgetDto.getEndDate(),
                mockBudgetDto.getBudgetStatus()
        );
    }

    @Test
    void updateBudget_NotFound() {
        when(budgetService.updateBudget(
                "invalidId",
                mockBudgetDto.getUserId(),
                mockBudgetDto.getBudgetCategory(),
                mockBudgetDto.getAmount(),
                mockBudgetDto.getStartDate(),
                mockBudgetDto.getEndDate(),
                mockBudgetDto.getBudgetStatus()
        )).thenReturn(Optional.empty());

        ResponseEntity<String> response = budgetController.updateBudget("invalidId", mockBudgetDto);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void deleteBudget_Success() {
        when(budgetService.deleteBudget("65f8b123abc123d456e78901")).thenReturn(true);

        ResponseEntity<String> response = budgetController.deleteBudget("65f8b123abc123d456e78901");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Budget deleted successfully", response.getBody());
    }

    @Test
    void deleteBudget_NotFound() {
        when(budgetService.deleteBudget("invalidId")).thenReturn(false);

        ResponseEntity<String> response = budgetController.deleteBudget("invalidId");

        assertEquals(404, response.getStatusCodeValue());
    }
}
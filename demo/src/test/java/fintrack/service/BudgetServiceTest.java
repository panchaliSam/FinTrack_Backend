package fintrack.service;

import com.fintrack.entity.Budget;
import com.fintrack.repository.BudgetRepository;
import com.fintrack.service.BudgetService;
import com.fintrack.type.BudgetStatus;
import com.fintrack.type.Category;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @InjectMocks
    private BudgetService budgetService;

    private Budget mockBudget;
    private ObjectId budgetId;

    @BeforeEach
    void setUp() {
        budgetId = new ObjectId(); // Simulating MongoDB auto-generated ID

        mockBudget = new Budget();
        mockBudget.setBudgetId(budgetId.toString());
        mockBudget.setUserId(new ObjectId().toString()); // User ID is also an ObjectId
        mockBudget.setBudgetCategory(Category.EXPENSE);
        mockBudget.setAmount(5000);
        mockBudget.setStartDate(LocalDateTime.of(2024, 1, 1, 0, 0));
        mockBudget.setEndDate(LocalDateTime.of(2025, 1, 1, 0, 0));
        mockBudget.setBudgetStatus(BudgetStatus.ACTIVE);
    }

    @Test
    void createBudget_Success() {
        when(budgetRepository.findByUserIdAndBudgetCategoryAndAmountAndStartDateAndEndDate(
                mockBudget.getUserId(), mockBudget.getBudgetCategory(), mockBudget.getAmount(),
                mockBudget.getStartDate(), mockBudget.getEndDate()
        )).thenReturn(Optional.empty());

        when(budgetRepository.save(any(Budget.class))).thenReturn(mockBudget);

        Budget createdBudget = budgetService.createBudget(
                mockBudget.getUserId(),
                mockBudget.getBudgetCategory(),
                mockBudget.getAmount(),
                mockBudget.getStartDate(),
                mockBudget.getEndDate(),
                mockBudget.getBudgetStatus()
        );

        assertNotNull(createdBudget);
        assertEquals(mockBudget.getAmount(), createdBudget.getAmount());
        verify(budgetRepository, times(1)).save(any(Budget.class));
    }

    @Test
    void createBudget_FailsDueToDuplicateEntry() {
        when(budgetRepository.findByUserIdAndBudgetCategoryAndAmountAndStartDateAndEndDate(
                mockBudget.getUserId(), mockBudget.getBudgetCategory(), mockBudget.getAmount(),
                mockBudget.getStartDate(), mockBudget.getEndDate()
        )).thenReturn(Optional.of(mockBudget));

        Exception exception = assertThrows(ResponseStatusException.class, () ->
                budgetService.createBudget(
                        mockBudget.getUserId(),
                        mockBudget.getBudgetCategory(),
                        mockBudget.getAmount(),
                        mockBudget.getStartDate(),
                        mockBudget.getEndDate(),
                        mockBudget.getBudgetStatus()
                ));

        assertTrue(exception.getMessage().contains("This budget entry already exists."));
    }

    @Test
    void createBudget_FailsDueToInvalidDuration() {
        LocalDateTime invalidEndDate = LocalDateTime.of(2024, 6, 1, 0, 0);

        Exception exception = assertThrows(ResponseStatusException.class, () ->
                budgetService.createBudget(
                        mockBudget.getUserId(),
                        mockBudget.getBudgetCategory(),
                        mockBudget.getAmount(),
                        mockBudget.getStartDate(),
                        invalidEndDate,
                        mockBudget.getBudgetStatus()
                ));

        assertTrue(exception.getMessage().contains("Budget duration must be exactly 12 months."));
    }

    @Test
    void getAllBudgets_Success() {
        when(budgetRepository.findAll()).thenReturn(List.of(mockBudget));

        List<Budget> budgets = budgetService.getAllBudgets();

        assertFalse(budgets.isEmpty());
        assertEquals(1, budgets.size());
    }

    @Test
    void getAllBudgets_EmptyList() {
        when(budgetRepository.findAll()).thenReturn(List.of());

        List<Budget> budgets = budgetService.getAllBudgets();

        assertTrue(budgets.isEmpty());
    }

    @Test
    void getBudgetById_Found() {
        when(budgetRepository.findById(budgetId.toString())).thenReturn(Optional.of(mockBudget));

        Optional<Budget> budget = budgetService.getBudgetById(budgetId.toString());

        assertTrue(budget.isPresent());
        assertEquals(mockBudget.getAmount(), budget.get().getAmount());
    }

    @Test
    void getBudgetById_NotFound() {
        when(budgetRepository.findById("nonexistentId")).thenReturn(Optional.empty());

        Optional<Budget> budget = budgetService.getBudgetById("nonexistentId");

        assertFalse(budget.isPresent());
    }

    @Test
    void updateBudget_Success() {
        when(budgetRepository.findById(budgetId.toString())).thenReturn(Optional.of(mockBudget));
        when(budgetRepository.save(any(Budget.class))).thenReturn(mockBudget);

        Optional<Budget> updatedBudget = budgetService.updateBudget(
                budgetId.toString(),
                mockBudget.getUserId(),
                Category.INCOME,
                7000,
                mockBudget.getStartDate(),
                mockBudget.getEndDate(),
                BudgetStatus.ACTIVE
        );

        assertTrue(updatedBudget.isPresent());
        assertEquals(7000, updatedBudget.get().getAmount());
        assertEquals(BudgetStatus.ACTIVE, updatedBudget.get().getBudgetStatus());
    }

    @Test
    void updateBudget_NotFound() {
        when(budgetRepository.findById("nonexistentId")).thenReturn(Optional.empty());

        Optional<Budget> updatedBudget = budgetService.updateBudget(
                "nonexistentId",
                mockBudget.getUserId(),
                mockBudget.getBudgetCategory(),
                mockBudget.getAmount(),
                mockBudget.getStartDate(),
                mockBudget.getEndDate(),
                mockBudget.getBudgetStatus()
        );

        assertFalse(updatedBudget.isPresent());
    }

    @Test
    void deleteBudget_Success() {
        when(budgetRepository.findById(budgetId.toString())).thenReturn(Optional.of(mockBudget));
        doNothing().when(budgetRepository).delete(mockBudget);

        boolean result = budgetService.deleteBudget(budgetId.toString());

        assertTrue(result);
        verify(budgetRepository, times(1)).delete(mockBudget);
    }

    @Test
    void deleteBudget_NotFound() {
        when(budgetRepository.findById("nonexistentId")).thenReturn(Optional.empty());

        boolean result = budgetService.deleteBudget("nonexistentId");

        assertFalse(result);
    }
}
package fintrack.repository;

import com.fintrack.entity.Budget;
import com.fintrack.repository.BudgetRepository;
import com.fintrack.type.Category;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetRepositoryTest {

    @Mock
    private BudgetRepository budgetRepository;

    private Budget budget;
    private String userId;

    @BeforeEach
    void setUp() {
        userId = new ObjectId().toString();
        budget = new Budget();
        budget.setBudgetId(new ObjectId().toString()); 
        budget.setUserId(userId);
        budget.setBudgetCategory(Category.EXPENSE);
        budget.setAmount(5000.00);
        budget.setStartDate(LocalDateTime.of(2024, 1, 1, 0, 0));
        budget.setEndDate(LocalDateTime.of(2024, 12, 31, 23, 59));
    }

    @Test
    void findByUserIdAndBudgetCategoryAndAmountAndStartDateAndEndDate_Success() {
        when(budgetRepository.findByUserIdAndBudgetCategoryAndAmountAndStartDateAndEndDate(
                userId, budget.getBudgetCategory(), budget.getAmount(), budget.getStartDate(), budget.getEndDate()))
                .thenReturn(Optional.of(budget));

        Optional<Budget> retrievedBudget = budgetRepository.findByUserIdAndBudgetCategoryAndAmountAndStartDateAndEndDate(
                userId, budget.getBudgetCategory(), budget.getAmount(), budget.getStartDate(), budget.getEndDate());

        assertTrue(retrievedBudget.isPresent());
        assertEquals(budget.getBudgetId(), retrievedBudget.get().getBudgetId());
        assertEquals(userId, retrievedBudget.get().getUserId());
        assertEquals(budget.getAmount(), retrievedBudget.get().getAmount());
        assertEquals(budget.getStartDate(), retrievedBudget.get().getStartDate());
        assertEquals(budget.getEndDate(), retrievedBudget.get().getEndDate());

        verify(budgetRepository, times(1)).findByUserIdAndBudgetCategoryAndAmountAndStartDateAndEndDate(
                userId, budget.getBudgetCategory(), budget.getAmount(), budget.getStartDate(), budget.getEndDate());
    }

    @Test
    void findByUserIdAndBudgetCategoryAndAmountAndStartDateAndEndDate_NotFound() {
        when(budgetRepository.findByUserIdAndBudgetCategoryAndAmountAndStartDateAndEndDate(
                userId, budget.getBudgetCategory(), budget.getAmount(), budget.getStartDate(), budget.getEndDate()))
                .thenReturn(Optional.empty());

        Optional<Budget> retrievedBudget = budgetRepository.findByUserIdAndBudgetCategoryAndAmountAndStartDateAndEndDate(
                userId, budget.getBudgetCategory(), budget.getAmount(), budget.getStartDate(), budget.getEndDate());

        assertFalse(retrievedBudget.isPresent());

        verify(budgetRepository, times(1)).findByUserIdAndBudgetCategoryAndAmountAndStartDateAndEndDate(
                userId, budget.getBudgetCategory(), budget.getAmount(), budget.getStartDate(), budget.getEndDate());
    }
}
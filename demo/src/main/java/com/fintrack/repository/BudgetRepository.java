package com.fintrack.repository;

import com.fintrack.entity.Budget;
import com.fintrack.type.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends MongoRepository<Budget, String> {
    Optional<Budget> findByUserIdAndBudgetCategoryAndAmountAndStartDateAndEndDate(
            String userId, Category budgetCategory, double amount, LocalDateTime startDate, LocalDateTime endDate);
}

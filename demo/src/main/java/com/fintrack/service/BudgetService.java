package com.fintrack.service;

import com.fintrack.entity.Budget;
import com.fintrack.type.BudgetStatus;
import com.fintrack.type.Category;
import com.fintrack.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class BudgetService {

    @Autowired
    private final BudgetRepository budgetRepository;

    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    public Budget createBudget(String userId, Category budgetCategory, double amount, LocalDateTime startDate, LocalDateTime endDate, BudgetStatus budgetStatus) {
           validateBudgetDates(startDate, endDate);
            validateDuplicateBudget(userId, budgetCategory, amount, startDate, endDate);

            Budget budget = new Budget();
            budget.setUserId(userId);
            budget.setBudgetCategory(budgetCategory);
            budget.setAmount(amount);
            budget.setStartDate(startDate);
            budget.setEndDate(endDate);
            budget.setBudgetStatus(budgetStatus);
            return budgetRepository.save(budget);
    }

    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    public Optional<Budget> getBudgetById(String budgetId) {
        return budgetRepository.findById(budgetId);
    }

    public Optional<Budget> updateBudget(String budgetId, String userId, Category budgetCategory, double amount, LocalDateTime startDate, LocalDateTime endDate, BudgetStatus budgetStatus){
        validateBudgetDates(startDate, endDate);
        validateDuplicateBudget(userId, budgetCategory, amount, startDate, endDate);

        Optional<Budget> existingBudget = budgetRepository.findById(budgetId);
        if(existingBudget.isPresent()){
            Budget budget = existingBudget.get();
            budget.setUserId(userId);
            budget.setBudgetCategory(budgetCategory);
            budget.setAmount(amount);
            budget.setStartDate(startDate);
            budget.setEndDate(endDate);
            budget.setBudgetStatus(budgetStatus);
            return Optional.of(budgetRepository.save(budget));
        }
        return Optional.empty();
    }

    private void validateBudgetDates(LocalDateTime startDate, LocalDateTime endDate){
        long monthsBetween = ChronoUnit.MONTHS.between(startDate, endDate);
        if(monthsBetween != 12){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Budget duration must be exactly 12 months.");
        }
    }


    private void validateDuplicateBudget(String userId, Category budgetCategory, double amount, LocalDateTime startDate, LocalDateTime endDate) {
        Optional<Budget> existingBudget = budgetRepository.findByUserIdAndBudgetCategoryAndAmountAndStartDateAndEndDate(userId, budgetCategory, amount, startDate, endDate);

        if (existingBudget.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This budget entry already exists.");
        }
    }

    public boolean deleteBudget(String budgetId) {
        Optional<Budget> existingBudget = budgetRepository.findById(budgetId);
        if(existingBudget.isPresent()){
            budgetRepository.delete(existingBudget.get());
            return true;
        }
        return false;
    }
}

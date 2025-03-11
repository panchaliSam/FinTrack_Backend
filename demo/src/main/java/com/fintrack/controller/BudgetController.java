package com.fintrack.controller;

import com.fintrack.dto.BudgetDto;
import com.fintrack.entity.Budget;
import com.fintrack.service.BudgetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/budget")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping
    public ResponseEntity<String>  createBudget(@RequestBody BudgetDto budgetDTO) {
        budgetService.createBudget(
                budgetDTO.getUserId(),
                budgetDTO.getBudgetCategory(),
                budgetDTO.getAmount(),
                budgetDTO.getStartDate(),
                budgetDTO.getEndDate(),
                budgetDTO.getBudgetStatus()
        );
        return ResponseEntity.ok("Budget created successfully");
    }

    @GetMapping
    public ResponseEntity<List<Budget>> getAllBudgets() {
        List<Budget> budgets = budgetService.getAllBudgets();
        return budgets.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(budgets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Budget> getBudgetById(@PathVariable String id) {
        Optional<Budget> budget = budgetService.getBudgetById(id);
        return budget.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateBudget(@PathVariable String id, @RequestBody BudgetDto budgetDTO) {
        Optional<Budget> updateBudget = budgetService.updateBudget(
                id,
                budgetDTO.getUserId(),
                budgetDTO.getBudgetCategory(),
                budgetDTO.getAmount(),
                budgetDTO.getStartDate(),
                budgetDTO.getEndDate(),
                budgetDTO.getBudgetStatus()
        );
        return updateBudget.map(budget -> ResponseEntity.ok("Budget updated successfully"))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBudget(@PathVariable String id) {
        if (budgetService.deleteBudget(id)) {
            return ResponseEntity.ok("Budget deleted successfully");
        }else{
            return ResponseEntity.notFound().build();
        }
    }
}

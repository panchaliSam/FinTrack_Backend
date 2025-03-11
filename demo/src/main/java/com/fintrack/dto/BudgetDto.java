package com.fintrack.dto;

import com.fintrack.type.BudgetStatus;
import com.fintrack.type.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetDto {
    private String userId;
    private Category budgetCategory;
    private double amount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BudgetStatus budgetStatus;
}
